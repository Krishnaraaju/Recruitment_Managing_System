package com.campus.recruitment.controller;

import com.campus.recruitment.dto.request.StudentProfileRequest;
import com.campus.recruitment.dto.response.ApiResponse;
import com.campus.recruitment.dto.response.StudentProfileResponse;
import com.campus.recruitment.security.CustomUserDetails;
import com.campus.recruitment.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        StudentProfileResponse response = studentService.getProfile(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Profile fetched successfully", response));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody StudentProfileRequest request) {
        StudentProfileResponse response = studentService.updateProfile(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }

    @GetMapping("/profile/{id}")
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> getStudentProfileById(@PathVariable Long id) {
        StudentProfileResponse response = studentService.getProfileById(id);
        return ResponseEntity.ok(ApiResponse.success("Profile fetched successfully", response));
    }
}
