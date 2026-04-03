package com.campus.recruitment.repository;

import com.campus.recruitment.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findAllByStudentId(Long studentId);
    Optional<Resume> findByIdAndStudentId(Long id, Long studentId);
    Optional<Resume> findByStudentIdAndIsPrimaryTrue(Long studentId);
}
