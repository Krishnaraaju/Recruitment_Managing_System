package com.campus.recruitment.service;

import com.campus.recruitment.dto.request.ApplicationRequest;
import com.campus.recruitment.dto.response.ApplicationResponse;
import com.campus.recruitment.entity.enums.ApplicationStatus;
import org.springframework.data.domain.Page;

public interface ApplicationService {
    ApplicationResponse apply(Long studentUserId, ApplicationRequest request);
    ApplicationResponse updateStatus(Long recruiterUserId, Long applicationId, ApplicationStatus status);
    ApplicationResponse getApplicationById(Long applicationId, Long userId);
    Page<ApplicationResponse> getMyApplications(Long studentUserId, int page, int size);
    Page<ApplicationResponse> getApplicationsForJob(Long recruiterUserId, Long jobId, int page, int size);
}
