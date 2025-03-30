package com.expenses.tracker.repository;

import com.expenses.tracker.entity.Expense;
import com.expenses.tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query(value = "SELECT u FROM User u where u.id = (SELECT e.user.id FROM Expense e where e.id = :id)")
    public User findUserByExpenseId(Long id);

}
