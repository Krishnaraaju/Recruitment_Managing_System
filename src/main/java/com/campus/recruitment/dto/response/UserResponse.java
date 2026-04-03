package com.campus.recruitment.dto.response;

import com.campus.recruitment.entity.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponse {
    private Long id;
    private String email;
    private Role role;
    private boolean isActive;
    private LocalDateTime createdAt;
}
