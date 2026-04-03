package com.campus.recruitment.dto.request;

import com.campus.recruitment.entity.enums.JobType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class JobPostRequest {

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Job type is required")
    private JobType type;

    private String location;

    @NotBlank(message = "Description is required")
    private String description;

    private String requirements;

    private BigDecimal stipend;

    @NotNull(message = "Deadline is required")
    @Future(message = "Deadline must be in the future")
    private LocalDate deadline;

    private List<String> skills;
}
