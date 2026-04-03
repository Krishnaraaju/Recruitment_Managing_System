package com.campus.recruitment.controller;

import com.campus.recruitment.dto.request.ApplicationRequest;
import com.campus.recruitment.dto.response.ApiResponse;
import com.campus.recruitment.dto.response.ApplicationResponse;
import com.campus.recruitment.entity.enums.ApplicationStatus;
import com.campus.recruitment.security.CustomUserDetails;
import com.campus.recruitment.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> applyForJob(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ApplicationRequest request) {
        ApplicationResponse response = applicationService.apply(userDetails.getId(), request);
        return new ResponseEntity<>(ApiResponse.success("Application submitted successfully", response), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateApplicationStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @RequestParam ApplicationStatus status) {
        ApplicationResponse response = applicationService.updateStatus(userDetails.getId(), id, status);
        return ResponseEntity.ok(ApiResponse.success("Application status updated", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getApplicationById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        ApplicationResponse response = applicationService.getApplicationById(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Application retrieved", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Page<ApplicationResponse>>> getMyApplications(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ApplicationResponse> applications = applicationService.getMyApplications(userDetails.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success("Your applications retrieved", applications));
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<Page<ApplicationResponse>>> getApplicationsForJob(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ApplicationResponse> applications = applicationService.getApplicationsForJob(userDetails.getId(), jobId, page, size);
        return ResponseEntity.ok(ApiResponse.success("Job applications retrieved", applications));
    }
}
