package com.expenses.tracker.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private String email;

}
