package com.campus.recruitment.service;

import com.campus.recruitment.dto.response.AdminDashboardResponse;
import com.campus.recruitment.dto.response.UserResponse;
import org.springframework.data.domain.Page;

public interface AdminService {
    AdminDashboardResponse getDashboardStats();
    Page<UserResponse> getAllUsers(int page, int size);
    void updateUserStatus(Long userId, boolean isActive);
    void approveRecruiter(Long recruiterProfileId);
}
