package com.campus.recruitment.service.impl;

import com.campus.recruitment.dto.response.AdminDashboardResponse;
import com.campus.recruitment.dto.response.UserResponse;
import com.campus.recruitment.entity.RecruiterProfile;
import com.campus.recruitment.entity.User;
import com.campus.recruitment.entity.enums.Role;
import com.campus.recruitment.exception.ResourceNotFoundException;
import com.campus.recruitment.repository.ApplicationRepository;
import com.campus.recruitment.repository.JobPostRepository;
import com.campus.recruitment.repository.RecruiterProfileRepository;
import com.campus.recruitment.repository.StudentProfileRepository;
import com.campus.recruitment.repository.UserRepository;
import com.campus.recruitment.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final JobPostRepository jobPostRepository;
    private final ApplicationRepository applicationRepository;

    public AdminServiceImpl(UserRepository userRepository,
                            StudentProfileRepository studentProfileRepository,
                            RecruiterProfileRepository recruiterProfileRepository,
                            JobPostRepository jobPostRepository,
                            ApplicationRepository applicationRepository) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.jobPostRepository = jobPostRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    public AdminDashboardResponse getDashboardStats() {
        return AdminDashboardResponse.builder()
                .totalStudents(studentProfileRepository.count())
                .totalRecruiters(recruiterProfileRepository.count())
                .pendingRecruiterApprovals(recruiterProfileRepository.findAll().stream().filter(r -> !r.isApproved()).count())
                .totalJobs(jobPostRepository.count()) // simplistic count
                .totalInternships(0) // replace with actual filtered query
                .totalApplications(applicationRepository.count())
                .build();
    }

    @Override
    public Page<UserResponse> getAllUsers(int page, int size) {
        Page<User> users = userRepository.findAll(PageRequest.of(page, size));
        return users.map(user -> {
            UserResponse res = new UserResponse();
            res.setId(user.getId());
            res.setEmail(user.getEmail());
            res.setRole(user.getRole());
            res.setActive(user.isActive());
            res.setCreatedAt(user.getCreatedAt());
            return res;
        });
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, boolean isActive) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        // Cannot deactivate yourself if you are admin, but skipping check for simplicity
        user.setActive(isActive);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void approveRecruiter(Long recruiterProfileId) {
        RecruiterProfile profile = recruiterProfileRepository.findById(recruiterProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found"));
                
        profile.setApproved(true);
        recruiterProfileRepository.save(profile);
    }
}
