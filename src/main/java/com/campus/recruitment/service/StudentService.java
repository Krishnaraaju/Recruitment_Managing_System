package com.campus.recruitment.service;

import com.campus.recruitment.dto.request.StudentProfileRequest;
import com.campus.recruitment.dto.response.StudentProfileResponse;

public interface StudentService {
    StudentProfileResponse getProfile(Long userId);
    StudentProfileResponse updateProfile(Long userId, StudentProfileRequest request);
    StudentProfileResponse getProfileById(Long profileId);
}
