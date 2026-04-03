package com.campus.recruitment.controller;

import com.campus.recruitment.dto.response.AdminDashboardResponse;
import com.campus.recruitment.dto.response.ApiResponse;
import com.campus.recruitment.dto.response.UserResponse;
import com.campus.recruitment.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved", adminService.getDashboardStats()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success("Users retrieved", adminService.getAllUsers(page, size)));
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable Long id,
            @RequestParam boolean isActive) {
        adminService.updateUserStatus(id, isActive);
        return ResponseEntity.ok(ApiResponse.success("User status updated", null));
    }

    @PutMapping("/recruiters/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveRecruiter(@PathVariable Long id) {
        adminService.approveRecruiter(id);
        return ResponseEntity.ok(ApiResponse.success("Recruiter approved successfully", null));
    }
}
