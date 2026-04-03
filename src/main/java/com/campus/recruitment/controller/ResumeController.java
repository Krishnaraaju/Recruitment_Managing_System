package com.campus.recruitment.controller;

import com.campus.recruitment.dto.response.ApiResponse;
import com.campus.recruitment.dto.response.ResumeResponse;
import com.campus.recruitment.security.CustomUserDetails;
import com.campus.recruitment.service.ResumeService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<ResumeResponse>> uploadResume(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        ResumeResponse response = resumeService.uploadResume(userDetails.getId(), file);
        return new ResponseEntity<>(ApiResponse.success("Resume uploaded successfully", response), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<ResumeResponse>>> getMyResumes(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ResumeResponse> responses = resumeService.getMyResumes(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Resumes retrieved successfully", responses));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadResume(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        Resource resource = resumeService.downloadResume(id, userDetails.getId());
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Void>> deleteResume(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        resumeService.deleteResume(userDetails.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Resume deleted successfully", null));
    }

    @PutMapping("/{id}/primary")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<ResumeResponse>> setPrimaryResume(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        ResumeResponse response = resumeService.setPrimaryResume(userDetails.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Primary resume updated", response));
    }
}
