package com.campus.recruitment.service;

import com.campus.recruitment.dto.request.JobPostRequest;
import com.campus.recruitment.dto.response.JobPostResponse;
import org.springframework.data.domain.Page;

public interface JobPostService {
    JobPostResponse createJobPost(Long recruiterUserId, JobPostRequest request);
    JobPostResponse updateJobPost(Long recruiterUserId, Long jobPostId, JobPostRequest request);
    void deleteJobPost(Long recruiterUserId, Long jobPostId);
    void adminDeleteJobPost(Long jobPostId);
    JobPostResponse getJobPostById(Long jobPostId);
    Page<JobPostResponse> getAllActiveJobs(int page, int size, String sortDir, String sortField);
    Page<JobPostResponse> getJobsByRecruiter(Long recruiterUserId, int page, int size);
}
