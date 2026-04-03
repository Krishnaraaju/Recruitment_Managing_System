package com.campus.recruitment.dto.response;

import com.campus.recruitment.entity.enums.InterviewMode;
import com.campus.recruitment.entity.enums.InterviewStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InterviewScheduleResponse {
    private Long id;
    private Long applicationId;
    private LocalDateTime scheduledAt;
    private InterviewMode mode;
    private String meetLink;
    private String notes;
    private InterviewStatus status;
}
