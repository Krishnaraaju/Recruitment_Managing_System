package com.campus.recruitment.service;

import com.campus.recruitment.dto.request.InterviewScheduleRequest;
import com.campus.recruitment.dto.response.InterviewScheduleResponse;
import com.campus.recruitment.entity.enums.InterviewStatus;

public interface InterviewService {
    InterviewScheduleResponse scheduleInterview(Long recruiterUserId, InterviewScheduleRequest request);
    InterviewScheduleResponse updateInterviewStatus(Long recruiterUserId, Long interviewId, InterviewStatus status);
    InterviewScheduleResponse getInterviewById(Long interviewId, Long userId);
}
