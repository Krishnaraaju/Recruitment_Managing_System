package com.campus.recruitment.controller;

import com.campus.recruitment.dto.request.JobPostRequest;
import com.campus.recruitment.dto.response.ApiResponse;
import com.campus.recruitment.dto.response.JobPostResponse;
import com.campus.recruitment.security.CustomUserDetails;
import com.campus.recruitment.service.JobPostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobPostController {

    private final JobPostService jobPostService;

    public JobPostController(JobPostService jobPostService) {
        this.jobPostService = jobPostService;
    }

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<JobPostResponse>> createJobPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody JobPostRequest request) {
        JobPostResponse response = jobPostService.createJobPost(userDetails.getId(), request);
        return new ResponseEntity<>(ApiResponse.success("Job post created successfully", response), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<JobPostResponse>> updateJobPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody JobPostRequest request) {
        JobPostResponse response = jobPostService.updateJobPost(userDetails.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Job post updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteJobPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
            
        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            jobPostService.adminDeleteJobPost(id);
        } else {
            jobPostService.deleteJobPost(userDetails.getId(), id);
        }
        
        return ResponseEntity.ok(ApiResponse.success("Job post deleted successfully", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobPostResponse>> getJobPostById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Job post retrieved successfully", jobPostService.getJobPostById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<JobPostResponse>>> getAllActiveJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(defaultValue = "createdAt") String sortField) {
        
        Page<JobPostResponse> jobs = jobPostService.getAllActiveJobs(page, size, sortDir, sortField);
        return ResponseEntity.ok(ApiResponse.success("Jobs retrieved successfully", jobs));
    }

    @GetMapping("/recruiter")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<Page<JobPostResponse>>> getJobsByRecruiter(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<JobPostResponse> jobs = jobPostService.getJobsByRecruiter(userDetails.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success("Your job posts retrieved successfully", jobs));
    }
}
