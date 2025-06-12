package com.expenses.tracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "group_expense")
public class GroupExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private BigDecimal amount;

//    @ManyToOne
//    @JoinColumn(name = "paid_by_user_id")
//    private User paidBy;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private ExpenseGroup group;

    @OneToMany(mappedBy = "groupExpense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseSplit> splits;

    private LocalDateTime date;
}
