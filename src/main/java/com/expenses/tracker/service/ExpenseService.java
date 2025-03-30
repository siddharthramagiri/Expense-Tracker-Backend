package com.expenses.tracker.service;

import com.expenses.tracker.entity.Expense;
import com.expenses.tracker.entity.User;
import com.expenses.tracker.repository.ExpenseRepository;
import com.expenses.tracker.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository, JwtService jwtService) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public Expense addExpense(Expense expense) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new RuntimeException("USER NOT FOUND... Login Again");
        }
        if(expense.getName() == null || expense.getAmount() == null ||
                expense.getCategory() == null || expense.getDate() == null) {
            throw new RuntimeException("Some fields are Empty");
        }
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    public ResponseEntity<List<Expense>> getAllExpenses(String token) throws RuntimeException{

        String email = jwtService.extractUsername(token.substring(7));
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new RuntimeException("USER NOT FOUND... Login Again");
        }
        List<Expense> expenses = user.getExpenses();
        return ResponseEntity.ok(expenses);
    }

    public ResponseEntity<Object> deleteExpense(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!authentication.isAuthenticated()) {
            return new ResponseEntity<>("User not Authorized", HttpStatus.UNAUTHORIZED);
        }
        if(expenseRepository.existsById(id) &&
                expenseRepository.findUserByExpenseId(id).getEmail()
                        .equals(authentication.getName())
        ) {
                expenseRepository.deleteById(id);
                return ResponseEntity.ok("Expense deleted successfully");
        } else {
            return new ResponseEntity<>("Expense Doesn't Exists", HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Object> updateExpense(Long id, Expense updatedExpense) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
            return new ResponseEntity<>("User not Authorized", HttpStatus.UNAUTHORIZED);
        }
        Optional<Expense> optionalExpense = expenseRepository.findById(id);

        if(optionalExpense.isEmpty() &&
                !expenseRepository.findUserByExpenseId(id).getEmail()
                .equals(authentication.getName())) {
            return new ResponseEntity<>("Expense Doesn't Exists", HttpStatus.NOT_FOUND);
        }
        Expense expense = optionalExpense.get();

        expense.setAmount(updatedExpense.getAmount());
        expense.setCategory(updatedExpense.getCategory());
        expense.setDate(updatedExpense.getDate());
        expense.setName(updatedExpense.getName());

        expenseRepository.save(expense);
        return new ResponseEntity<>("Updated Expense Success Fully", HttpStatus.OK);
    }

}
