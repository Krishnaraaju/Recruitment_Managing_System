package com.campus.recruitment.service.impl;

import com.campus.recruitment.dto.request.ApplicationRequest;
import com.campus.recruitment.dto.response.ApplicationResponse;
import com.campus.recruitment.dto.response.InterviewScheduleResponse;
import com.campus.recruitment.entity.Application;
import com.campus.recruitment.entity.JobPost;
import com.campus.recruitment.entity.StudentProfile;
import com.campus.recruitment.entity.enums.ApplicationStatus;
import com.campus.recruitment.exception.BadRequestException;
import com.campus.recruitment.exception.ResourceNotFoundException;
import com.campus.recruitment.exception.UnauthorizedException;
import com.campus.recruitment.repository.ApplicationRepository;
import com.campus.recruitment.repository.JobPostRepository;
import com.campus.recruitment.repository.StudentProfileRepository;
import com.campus.recruitment.service.ApplicationService;
import com.campus.recruitment.service.EmailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobPostRepository jobPostRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final EmailService emailService;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository,
                                  JobPostRepository jobPostRepository,
                                  StudentProfileRepository studentProfileRepository,
                                  EmailService emailService) {
        this.applicationRepository = applicationRepository;
        this.jobPostRepository = jobPostRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public ApplicationResponse apply(Long studentUserId, ApplicationRequest request) {
        StudentProfile student = studentProfileRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        JobPost jobPost = jobPostRepository.findById(request.getJobPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Job Post not found"));

        if (jobPost.isDeleted()) {
            throw new BadRequestException("You cannot apply to a deleted job post");
        }

        if (applicationRepository.existsByStudentIdAndJobPostId(student.getId(), jobPost.getId())) {
            throw new BadRequestException("You have already applied to this job");
        }

        Application application = new Application();
        application.setStudent(student);
        application.setJobPost(jobPost);
        application.setCoverLetter(request.getCoverLetter());
        application.setStatus(ApplicationStatus.APPLIED);

        application = applicationRepository.save(application);

        // Notify recruiter via email
        String recruiterEmail = jobPost.getRecruiter().getUser().getEmail();
        emailService.sendEmail(recruiterEmail, 
                "New Application Received for " + jobPost.getTitle(),
                "A new application has been submitted by " + student.getFullName());

        return mapToResponse(application);
    }

    @Override
    @Transactional
    public ApplicationResponse updateStatus(Long recruiterUserId, Long applicationId, ApplicationStatus status) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        // Verify ownership
        if (!application.getJobPost().getRecruiter().getUser().getId().equals(recruiterUserId)) {
            throw new UnauthorizedException("You do not have permission to update applications for this job.");
        }

        application.setStatus(status);
        application = applicationRepository.save(application);

        // Notify student via email
        String studentEmail = application.getStudent().getUser().getEmail();
        emailService.sendEmail(studentEmail,
                "Application Status Update: " + application.getJobPost().getTitle(),
                "Your application status has been updated to: " + status.name());

        return mapToResponse(application);
    }

    @Override
    public ApplicationResponse getApplicationById(Long applicationId, Long userId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
                
        // Validation: user must be either the student who applied, or the recruiter who owns the job, or admin
        boolean isOwnerStudent = application.getStudent().getUser().getId().equals(userId);
        boolean isOwnerRecruiter = application.getJobPost().getRecruiter().getUser().getId().equals(userId);
        
        // Simple check (admin logic could be separated)
        if (!isOwnerStudent && !isOwnerRecruiter) {
            throw new UnauthorizedException("You are not authorized to view this application");
        }

        return mapToResponse(application);
    }

    @Override
    public Page<ApplicationResponse> getMyApplications(Long studentUserId, int page, int size) {
        StudentProfile student = studentProfileRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        Page<Application> applications = applicationRepository.findAllByStudentId(student.getId(), pageable);
        return applications.map(this::mapToResponse);
    }

    @Override
    public Page<ApplicationResponse> getApplicationsForJob(Long recruiterUserId, Long jobId, int page, int size) {
        JobPost jobPost = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job Post not found"));
                
        if (!jobPost.getRecruiter().getUser().getId().equals(recruiterUserId)) {
            throw new UnauthorizedException("You do not have permission to view applications for this job.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        Page<Application> applications = applicationRepository.findAllByJobPostId(jobId, pageable);
        return applications.map(this::mapToResponse);
    }

    private ApplicationResponse mapToResponse(Application application) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(application.getId());
        response.setStudentId(application.getStudent().getId());
        response.setStudentName(application.getStudent().getFullName());
        response.setJobPostId(application.getJobPost().getId());
        response.setJobTitle(application.getJobPost().getTitle());
        response.setStatus(application.getStatus());
        response.setCoverLetter(application.getCoverLetter());
        response.setAppliedAt(application.getAppliedAt());
        
        if (application.getInterviewSchedule() != null) {
            InterviewScheduleResponse scheduleResponse = new InterviewScheduleResponse();
            scheduleResponse.setId(application.getInterviewSchedule().getId());
            scheduleResponse.setScheduledAt(application.getInterviewSchedule().getScheduledAt());
            scheduleResponse.setMode(application.getInterviewSchedule().getMode());
            scheduleResponse.setMeetLink(application.getInterviewSchedule().getMeetLink());
            scheduleResponse.setStatus(application.getInterviewSchedule().getStatus());
            response.setInterviewSchedule(scheduleResponse);
        }
        
        return response;
    }
}
