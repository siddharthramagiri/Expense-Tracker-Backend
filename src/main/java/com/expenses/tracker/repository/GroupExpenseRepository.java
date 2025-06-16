package com.expenses.tracker.repository;

import com.expenses.tracker.entity.GroupExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface GroupExpenseRepository extends JpaRepository<GroupExpense, Long> {

//    @Query("SELECT SUM(e.amount) FROM GroupExpense e WHERE e.group.id = :groupId AND e.paidBy.id = :userId")
//    BigDecimal findTotalPaidByUserInGroup(@Param("groupId") Long groupId, @Param("userId") Long userId);

    @Query(value = "SELECT ge FROM GroupExpense ge JOIN ge.splits s WHERE s.user.id = :userId")
    List<GroupExpense> findGroupExpensesByUserId(@Param("userId") Long userId);

}
