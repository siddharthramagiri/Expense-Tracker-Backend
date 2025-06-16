package com.expenses.tracker.dto;


import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class GroupWithMemberExpensesDTO {
    private Long groupId;
    private String groupName;
    private Long createdById;
    private List<MemberExpenseDTO> memberExpenses;

}
