package com.campus.recruitment.service.impl;

import com.campus.recruitment.dto.request.InterviewScheduleRequest;
import com.campus.recruitment.dto.response.InterviewScheduleResponse;
import com.campus.recruitment.entity.Application;
import com.campus.recruitment.entity.InterviewSchedule;
import com.campus.recruitment.exception.ResourceNotFoundException;
import com.campus.recruitment.exception.UnauthorizedException;
import com.campus.recruitment.repository.ApplicationRepository;
import com.campus.recruitment.repository.InterviewScheduleRepository;
import com.campus.recruitment.service.EmailService;
import com.campus.recruitment.service.InterviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InterviewServiceImpl implements InterviewService {

    private final InterviewScheduleRepository interviewScheduleRepository;
    private final ApplicationRepository applicationRepository;
    private final EmailService emailService;

    public InterviewServiceImpl(InterviewScheduleRepository interviewScheduleRepository,
                                ApplicationRepository applicationRepository,
                                EmailService emailService) {
        this.interviewScheduleRepository = interviewScheduleRepository;
        this.applicationRepository = applicationRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public InterviewScheduleResponse scheduleInterview(Long recruiterUserId, InterviewScheduleRequest request) {
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!application.getJobPost().getRecruiter().getUser().getId().equals(recruiterUserId)) {
            throw new UnauthorizedException("You can only schedule interviews for your own job posts.");
        }

        InterviewSchedule schedule = new InterviewSchedule();
        schedule.setApplication(application);
        schedule.setScheduledAt(request.getScheduledAt());
        schedule.setMode(request.getMode());
        schedule.setMeetLink(request.getMeetLink());
        schedule.setNotes(request.getNotes());

        schedule = interviewScheduleRepository.save(schedule);

        // Notify Student
        emailService.sendEmail(application.getStudent().getUser().getEmail(),
                "Interview Scheduled for " + application.getJobPost().getTitle(),
                "An interview has been scheduled on " + schedule.getScheduledAt().toString() + 
                "\nMode: " + schedule.getMode() + "\nLink: " + schedule.getMeetLink());

        return mapToResponse(schedule);
    }

    @Override
    @Transactional
    public InterviewScheduleResponse updateInterviewStatus(Long recruiterUserId, Long interviewId, com.campus.recruitment.entity.enums.InterviewStatus status) {
        InterviewSchedule schedule = interviewScheduleRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));

        if (!schedule.getApplication().getJobPost().getRecruiter().getUser().getId().equals(recruiterUserId)) {
            throw new UnauthorizedException("You do not have permission to update this interview.");
        }

        schedule.setStatus(status);
        return mapToResponse(interviewScheduleRepository.save(schedule));
    }

    @Override
    public InterviewScheduleResponse getInterviewById(Long interviewId, Long userId) {
        InterviewSchedule schedule = interviewScheduleRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));

        Long studentUserId = schedule.getApplication().getStudent().getUser().getId();
        Long recruiterUserId = schedule.getApplication().getJobPost().getRecruiter().getUser().getId();

        if (!studentUserId.equals(userId) && !recruiterUserId.equals(userId)) {
            throw new UnauthorizedException("You do not have permission to view this interview.");
        }

        return mapToResponse(schedule);
    }

    private InterviewScheduleResponse mapToResponse(InterviewSchedule schedule) {
        InterviewScheduleResponse response = new InterviewScheduleResponse();
        response.setId(schedule.getId());
        response.setApplicationId(schedule.getApplication().getId());
        response.setScheduledAt(schedule.getScheduledAt());
        response.setMode(schedule.getMode());
        response.setMeetLink(schedule.getMeetLink());
        response.setNotes(schedule.getNotes());
        response.setStatus(schedule.getStatus());
        return response;
    }
}
