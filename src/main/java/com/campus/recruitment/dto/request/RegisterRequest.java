package com.campus.recruitment.dto.request;

import com.campus.recruitment.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Role is required")
    private Role role;

    @NotBlank(message = "Full name is required")
    private String fullName;
    
    // For Student
    private String phone;
    private String college;
    private String degree;
    private String branch;
    private Integer graduationYear;
    
    // For Recruiter
    private String designation;
    private Long companyId;
    
    // In a real scenario, you might split Registration into standard + specific profiles,
    // but a combined DTO is simpler for start.
}
