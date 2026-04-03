package com.campus.recruitment.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudentProfileResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String phone;
    private String college;
    private String degree;
    private String branch;
    private Integer graduationYear;
    private Double cgpa;
    private String bio;
    private List<String> skills;
}
