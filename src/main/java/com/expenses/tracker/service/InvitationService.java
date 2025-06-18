package com.expenses.tracker.service;


import com.expenses.tracker.dto.GroupInvitationsDTO;
import com.expenses.tracker.entity.ExpenseGroup;
import com.expenses.tracker.entity.GroupInvitation;
import com.expenses.tracker.entity.User;
import com.expenses.tracker.repository.ExpenseGroupRepository;
import com.expenses.tracker.repository.GroupInvitationRepository;
import com.expenses.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class InvitationService {
    @Value("${frontend.url}")
    String frontendUrl;

    String domain = "http://localhost:8080";

    private final GroupInvitationRepository groupInvitationRepository;
    private final ExpenseGroupRepository groupRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    public InvitationService(GroupInvitationRepository groupInvitationRepository, ExpenseGroupRepository groupRepository, UserRepository userRepository, JavaMailSender mailSender) {
        this.groupInvitationRepository = groupInvitationRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    public void sendGroupInvitation(Long groupId, String invitedUserEmail) {
        User invitedUser = userRepository.findByEmail(invitedUserEmail);
        ExpenseGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        String token = UUID.randomUUID().toString();
        GroupInvitation invitation = new GroupInvitation();
        invitation.setToken(token);
        invitation.setGroup(group);
        invitation.setInvitedUser(invitedUser);
        invitation.setCreatedAt(LocalDateTime.now());

        groupInvitationRepository.save(invitation);

//        String confirmationLink = domain + "/api/group/invite/confirm-invitation?token=" + token;
        String confirmationLink = frontendUrl+"/groups/invites";
        String subject = "Group Invitation: " + group.getName();
        String body = "You have been invited to join the group \"" + group.getName() + "\".\n"
                + "Click the link to confirm: " + confirmationLink;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(invitedUser.getEmail());
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }


    public ResponseEntity<String> confirmGroupInvitation(@RequestParam String token) {
        GroupInvitation invitation = groupInvitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));

        if (invitation.isAccepted()) {
            return ResponseEntity.ok("You already accepted this invitation.");
        }

        ExpenseGroup group = invitation.getGroup();
        User invitedUser = invitation.getInvitedUser();

        group.getMembers().add(invitedUser);
        invitation.setAccepted(true);

        groupRepository.save(group);
        groupInvitationRepository.save(invitation);

        return ResponseEntity.ok("Successfully joined group: " + group.getName());
    }

    public ResponseEntity<List<GroupInvitationsDTO>> getAllInvitations() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(!authentication.isAuthenticated()) {
                throw new RuntimeException("Not Authenticated");
            }
            String email = authentication.getName();
            User user = userRepository.findByEmail(email);
            List<GroupInvitation> invitations = groupInvitationRepository.findByInvitedUser_Id(user.getId());
            List<GroupInvitationsDTO> groupInvitationsDTOList = new ArrayList<>();
            for(GroupInvitation invitation : invitations) {
                GroupInvitationsDTO groupInvitation = getGroupInvitationsDTO(invitation);
                groupInvitationsDTOList.add(groupInvitation);
            }
            return new ResponseEntity<>(groupInvitationsDTOList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private static GroupInvitationsDTO getGroupInvitationsDTO(GroupInvitation invitation) {
        Long id = invitation.getId();
        String token = invitation.getToken();
        String groupName = invitation.getGroup().getName();
        boolean isAccepted = invitation.isAccepted();

        User createdByUser = invitation.getGroup().getCreatedBy();
        String createdBy = createdByUser.getUsername();
        String createdByEmail = createdByUser.getEmail();
        LocalDateTime invitedAt = invitation.getCreatedAt();
        return new GroupInvitationsDTO(id, token, groupName, createdBy, createdByEmail, isAccepted, invitedAt);
    }

    public ResponseEntity<String> declineInvitation(Long invitationId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(!authentication.isAuthenticated()) {
                throw new RuntimeException("Not Authenticated");
            }
            String email = authentication.getName();
            User user = userRepository.findByEmail(email);
            Optional<GroupInvitation> groupInvitation = groupInvitationRepository.findById(invitationId);
            if(groupInvitation.isEmpty()) {
                throw new RuntimeException("No Invitation Found");
            }
            if(!Objects.equals(groupInvitation.get().getInvitedUser().getId(), user.getId())) {
                throw new RuntimeException("Error Deleting the invitation of another user");
            }
            groupInvitationRepository.deleteById(invitationId);
            return new ResponseEntity<>("Invitation Declined", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
