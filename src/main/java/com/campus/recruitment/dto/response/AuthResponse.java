package com.campus.recruitment.dto.response;

import com.campus.recruitment.entity.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponse {
    private String accessToken;
    private String tokenType;
    private Role role;
    private Long expiresIn;
    private Long userId;
}
