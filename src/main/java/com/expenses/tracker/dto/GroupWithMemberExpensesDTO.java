package com.expenses.tracker.dto;


import java.util.List;

public class GroupWithMemberExpensesDTO {
    private Long groupId;
    private String groupName;
    private List<MemberExpenseDTO> memberExpenses;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<MemberExpenseDTO> getMemberExpenses() {
        return memberExpenses;
    }

    public void setMemberExpenses(List<MemberExpenseDTO> memberExpenses) {
        this.memberExpenses = memberExpenses;
    }
}
