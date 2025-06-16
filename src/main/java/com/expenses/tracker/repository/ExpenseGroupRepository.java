package com.expenses.tracker.repository;

import com.expenses.tracker.entity.ExpenseGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ExpenseGroupRepository extends JpaRepository<ExpenseGroup, Long> {

    @Query("SELECT g FROM ExpenseGroup g JOIN g.members m WHERE m.id = :userId")
    List<ExpenseGroup> findAllByMemberId(Long userId);


    @Query("SELECT g FROM ExpenseGroup g JOIN g.members m WHERE m.id = :userId")
    List<ExpenseGroup> findExpenseGroupsByMemberId(Long userId);

    List<ExpenseGroup> findByMembers_Id(Long userId);
}
