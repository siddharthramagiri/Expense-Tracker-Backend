package com.expenses.tracker.service;

import com.expenses.tracker.entity.Expense;
import com.expenses.tracker.entity.User;
import com.expenses.tracker.repository.ExpenseRepository;
import com.expenses.tracker.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
