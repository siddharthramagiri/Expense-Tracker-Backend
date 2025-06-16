package com.expenses.tracker.dto;

import java.util.List;

public class CreateGroupRequest {
    public String groupName;
    public Long createdByUserId;
//    public List<Long> memberIds;
    public List<String> memberEmails;
}