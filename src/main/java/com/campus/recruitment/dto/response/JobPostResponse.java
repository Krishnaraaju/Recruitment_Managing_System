package com.campus.recruitment.dto.response;

import com.campus.recruitment.entity.enums.JobStatus;
import com.campus.recruitment.entity.enums.JobType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class JobPostResponse {
    private Long id;
    private Long recruiterId;
    private String companyName;
    private String categoryName;
    private String title;
    private JobType type;
    private String location;
    private String description;
    private String requirements;
    private BigDecimal stipend;
    private LocalDate deadline;
    private JobStatus status;
    private List<String> skills;
    private LocalDateTime createdAt;
}
