package com.campus.recruitment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudentProfileRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;
    private String college;
    private String degree;
    private String branch;
    private Integer graduationYear;
    private Double cgpa;
    private String bio;
    
    private List<String> skills; // List of skill names
}
