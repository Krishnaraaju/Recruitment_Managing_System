package com.campus.recruitment.service;

import com.campus.recruitment.dto.response.ResumeResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ResumeService {
    ResumeResponse uploadResume(Long studentUserId, MultipartFile file);
    List<ResumeResponse> getMyResumes(Long studentUserId);
    Resource downloadResume(Long resumeId, Long userId);
    void deleteResume(Long studentUserId, Long resumeId);
    ResumeResponse setPrimaryResume(Long studentUserId, Long resumeId);
}
