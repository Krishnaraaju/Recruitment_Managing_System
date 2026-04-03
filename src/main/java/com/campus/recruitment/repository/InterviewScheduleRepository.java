package com.campus.recruitment.repository;

import com.campus.recruitment.entity.InterviewSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewScheduleRepository extends JpaRepository<InterviewSchedule, Long> {
    Optional<InterviewSchedule> findByApplicationId(Long applicationId);
    // You could also add dynamic queries for recruiter/student upcoming interviews
}
