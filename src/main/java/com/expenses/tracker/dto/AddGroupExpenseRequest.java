package com.expenses.tracker.dto;

import com.expenses.tracker.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class AddGroupExpenseRequest {
    public Long groupId;
    public String description;
    public BigDecimal amount;
    public LocalDateTime date;
    public String category;
    public Map<Long, BigDecimal> splitMap; // userId -> amount

    public String getCategory() {
        return this.category;
    }
    public void setCategory(String category) {
        this.category  = category;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Map<Long, BigDecimal> getSplitMap() {
        return splitMap;
    }

    public void setSplitMap(Map<Long, BigDecimal> splitMap) {
        this.splitMap = splitMap;
    }
}
