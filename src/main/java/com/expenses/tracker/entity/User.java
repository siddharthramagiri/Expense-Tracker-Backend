package com.expenses.tracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "app_user")
@RequiredArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String username;
    private LocalDate createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses;

    // Groups this user created
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ExpenseGroup> createdGroups;

    // Groups this user is a member of
    @ManyToMany(mappedBy = "members")
    @JsonIgnore
    private List<ExpenseGroup> groups;

    // Group expenses this user paid
    //    @OneToMany(mappedBy = "paidBy")
    //    private List<GroupExpense> paidExpenses;

    // Splits assigned to this user
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<ExpenseSplit> splits;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public List<ExpenseGroup> getCreatedGroups() {
        return createdGroups;
    }

    public void setCreatedGroups(List<ExpenseGroup> createdGroups) {
        this.createdGroups = createdGroups;
    }

    public List<ExpenseGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<ExpenseGroup> groups) {
        this.groups = groups;
    }

    public List<ExpenseSplit> getSplits() {
        return splits;
    }

    public void setSplits(List<ExpenseSplit> splits) {
        this.splits = splits;
    }
}
