package com.campus.recruitment.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AdminDashboardResponse {
    private long totalStudents;
    private long totalRecruiters;
    private long pendingRecruiterApprovals;
    private long totalJobs;
    private long totalInternships;
    private long totalApplications;
}
