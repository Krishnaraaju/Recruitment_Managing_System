package com.campus.recruitment.repository;

import com.campus.recruitment.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Page<Application> findAllByStudentId(Long studentId, Pageable pageable);
    Page<Application> findAllByJobPostId(Long jobPostId, Pageable pageable);
    boolean existsByStudentIdAndJobPostId(Long studentId, Long jobPostId);
}
