package com.campus.recruitment.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecruiterProfileResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String designation;
    private CompanyResponse company;
    private boolean isApproved;
}
