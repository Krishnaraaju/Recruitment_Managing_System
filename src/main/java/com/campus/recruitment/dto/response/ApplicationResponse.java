package com.campus.recruitment.dto.response;

import com.campus.recruitment.entity.enums.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApplicationResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long jobPostId;
    private String jobTitle;
    private ApplicationStatus status;
    private String coverLetter;
    private LocalDateTime appliedAt;
    private InterviewScheduleResponse interviewSchedule;
}
