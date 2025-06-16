package com.expenses.tracker.dto;

import com.expenses.tracker.entity.User;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class AddGroupExpenseRequest {
    public Long groupId;
    public String description;
    public BigDecimal amount;
    public LocalDateTime date;
    public String category;
    public Map<Long, BigDecimal> splitMap;

}
