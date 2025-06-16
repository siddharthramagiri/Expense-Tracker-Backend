package com.expenses.tracker.dto;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class CreateGroupRequest {
    private String groupName;
    private Long createdByUserId;
//    public List<Long> memberIds;
    private List<String> memberEmails;
}