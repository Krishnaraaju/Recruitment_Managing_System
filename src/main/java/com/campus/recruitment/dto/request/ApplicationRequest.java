package com.campus.recruitment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationRequest {

    @NotNull(message = "Job Post ID is required")
    private Long jobPostId;

    private String coverLetter;
}
