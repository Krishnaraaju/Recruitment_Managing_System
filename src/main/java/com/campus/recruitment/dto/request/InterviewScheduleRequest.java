package com.campus.recruitment.dto.request;

import com.campus.recruitment.entity.enums.InterviewMode;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InterviewScheduleRequest {

    @NotNull(message = "Application ID is required")
    private Long applicationId;

    @NotNull(message = "Schedule time is required")
    @Future(message = "Schedule time must be in the future")
    private LocalDateTime scheduledAt;

    @NotNull(message = "Interview mode is required")
    private InterviewMode mode;

    private String meetLink;
    private String notes;
}
