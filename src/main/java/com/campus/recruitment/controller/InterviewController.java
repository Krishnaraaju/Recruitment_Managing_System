package com.campus.recruitment.controller;

import com.campus.recruitment.dto.request.InterviewScheduleRequest;
import com.campus.recruitment.dto.response.ApiResponse;
import com.campus.recruitment.dto.response.InterviewScheduleResponse;
import com.campus.recruitment.entity.enums.InterviewStatus;
import com.campus.recruitment.security.CustomUserDetails;
import com.campus.recruitment.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<InterviewScheduleResponse>> scheduleInterview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody InterviewScheduleRequest request) {
        InterviewScheduleResponse response = interviewService.scheduleInterview(userDetails.getId(), request);
        return new ResponseEntity<>(ApiResponse.success("Interview scheduled successfully", response), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<InterviewScheduleResponse>> updateInterviewStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @RequestParam InterviewStatus status) {
        InterviewScheduleResponse response = interviewService.updateInterviewStatus(userDetails.getId(), id, status);
        return ResponseEntity.ok(ApiResponse.success("Interview status updated", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InterviewScheduleResponse>> getInterviewDetails(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        InterviewScheduleResponse response = interviewService.getInterviewById(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Interview details retrieved", response));
    }
}
