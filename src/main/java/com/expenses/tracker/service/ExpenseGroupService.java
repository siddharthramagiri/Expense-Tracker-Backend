package com.expenses.tracker.service;

import com.expenses.tracker.dto.AddGroupExpenseRequest;
import com.expenses.tracker.dto.CreateGroupRequest;
import com.expenses.tracker.dto.GroupWithMemberExpensesDTO;
import com.expenses.tracker.dto.MemberExpenseDTO;
import com.expenses.tracker.entity.*;
import com.expenses.tracker.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class ExpenseGroupService {

    private final UserRepository userRepository;
    private final ExpenseGroupRepository groupRepository;
    private final GroupExpenseRepository groupExpenseRepository;
    private final ExpenseRepository expenseRepository;
    private final InvitationService invitationService;

    public ExpenseGroupService(UserRepository userRepository, ExpenseGroupRepository groupRepository, GroupExpenseRepository groupExpenseRepository,
                               ExpenseRepository expenseRepository, InvitationService invitationService) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.groupExpenseRepository = groupExpenseRepository;
        this.expenseRepository = expenseRepository;
        this.invitationService = invitationService;
    }

    public ExpenseGroup createGroup(CreateGroupRequest req) {
        User creator = userRepository.findById(req.createdByUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<User> members = new ArrayList<>();
        members.add(creator);

        ExpenseGroup group = new ExpenseGroup();
        group.setName(req.groupName);
        group.setCreatedBy(creator);
        group.setMembers(members);
        groupRepository.save(group);

        for(User member : userRepository.findAllById(req.memberIds)) {
            Optional<User> user = userRepository.findById(member.getId());
            if(user.isEmpty()) {
                throw new RuntimeException("User Doesn't Exists");
            }
            invitationService.sendGroupInvitation(group.getId(), user.get().getEmail());
        }

        return group;
    }

    @Transactional
    public void addGroupExpense(AddGroupExpenseRequest request) {
        ExpenseGroup group = groupRepository.findById(request.groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        List<User> members = group.getMembers();
        int totalMembers = members.size();
        if (totalMembers == 0) {
            throw new RuntimeException("No members in the group.");
        }
        Map<Long, BigDecimal> splitMap = request.splitMap;
        if(!splitMap.isEmpty()) {
            for(Map.Entry<Long, BigDecimal> map : splitMap.entrySet()) {
                Optional<User> member = userRepository.findById(map.getKey());
                if(member.isEmpty())
                    continue;
                Expense expense = new Expense();
                expense.setAmount(map.getValue());
                expense.setName("[Group: " + group.getName() + "] " + request.getDescription());
                expense.setDate(request.getDate());
                expense.setUser(member.get());
                expense.setCategory(request.category);
                expense.setGroup(group);
                expenseRepository.save(expense);
            }
        } else {
            BigDecimal perPersonAmount = request.getAmount().divide(BigDecimal.valueOf(totalMembers), 2, RoundingMode.HALF_UP);
            for (User member : members) {
                Expense expense = new Expense();
                expense.setAmount(perPersonAmount);
                expense.setName("[Group: " + group.getName() + "] " + request.getDescription());
                expense.setDate(request.getDate());
                expense.setUser(member);
                expense.setCategory(request.category);
                expense.setGroup(group);
                expenseRepository.save(expense);
            }
        }
        GroupExpense groupExpense = new GroupExpense();
        groupExpense.setGroup(group);
        groupExpense.setDescription(request.getDescription());
        groupExpense.setAmount(request.getAmount());
        groupExpense.setDate(request.getDate());
        groupExpenseRepository.save(groupExpense);
    }

    public List<GroupWithMemberExpensesDTO> getGroupsWithMemberExpenses(Long userId) {
        // Step 1: Fetch all groups the user is a member of
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!authentication.isAuthenticated()) {
            throw new RuntimeException("Not Authenticated");
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        if(!user.getId().equals(userId)) {
            throw new RuntimeException("User Id Doesn't Match with the current Session's user");
        }
        List<ExpenseGroup> groups = groupRepository.findByMembers_Id(userId);

        List<GroupWithMemberExpensesDTO> result = new ArrayList<>();

        for (ExpenseGroup group : groups) {
            GroupWithMemberExpensesDTO dto = new GroupWithMemberExpensesDTO();
            dto.setGroupId(group.getId());
            dto.setGroupName(group.getName());
            dto.setCreatedById(group.getCreatedBy().getId());

            List<MemberExpenseDTO> memberExpenses = new ArrayList<>();

            for (User member : group.getMembers()) {
                // Step 2: Fetch personal expenses of the member in this group
                BigDecimal totalPaid = expenseRepository
                        .findByUserAndGroup(member.getId(), group.getId())
                        .stream()
                        .map(Expense::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                MemberExpenseDTO memberExpenseDTO = new MemberExpenseDTO();
                memberExpenseDTO.setMemberId(member.getId());
                memberExpenseDTO.setUsername(member.getUsername());
                memberExpenseDTO.setTotalPaid(totalPaid);

                memberExpenses.add(memberExpenseDTO);
            }

            dto.setMemberExpenses(memberExpenses);
            result.add(dto);
        }

        return result;
    }

    public ResponseEntity<String> deleteGroup(Long groupId) {
        try {
            ExpenseGroup group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));

            User createdBy = group.getCreatedBy();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if(!authentication.isAuthenticated()) {
                throw new RuntimeException("Not Authenticated");
            }

            String email = authentication.getName();
            User user = userRepository.findByEmail(email);

            if(!Objects.equals(createdBy.getId(), user.getId())) {
                throw new RuntimeException("You are not the Admin of the Group");
            }

            List<Expense> groupExpenses = expenseRepository.findByGroup_Id(groupId);
            expenseRepository.deleteAll(groupExpenses);

            groupRepository.deleteById(groupId);

            return new ResponseEntity<>("Deleted Group " + group.getName() + " Successfully", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<String> addAMemberToGroup(Long groupId, Long userId) {


        return new ResponseEntity<>("", HttpStatus.OK);
    }

}
