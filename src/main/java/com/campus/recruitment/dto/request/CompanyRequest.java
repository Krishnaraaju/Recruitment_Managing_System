package com.campus.recruitment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyRequest {
    @NotBlank(message = "Company name is required")
    private String name;

    private String website;
    private String industry;
    private String logoUrl;
}
