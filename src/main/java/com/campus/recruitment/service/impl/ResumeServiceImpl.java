package com.campus.recruitment.service.impl;

import com.campus.recruitment.dto.response.ResumeResponse;
import com.campus.recruitment.entity.Resume;
import com.campus.recruitment.entity.StudentProfile;
import com.campus.recruitment.entity.enums.Role;
import com.campus.recruitment.exception.ResourceNotFoundException;
import com.campus.recruitment.exception.UnauthorizedException;
import com.campus.recruitment.repository.ResumeRepository;
import com.campus.recruitment.repository.StudentProfileRepository;
import com.campus.recruitment.repository.UserRepository;
import com.campus.recruitment.service.FileStorageService;
import com.campus.recruitment.service.ResumeService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public ResumeServiceImpl(ResumeRepository resumeRepository,
                             StudentProfileRepository studentProfileRepository,
                             UserRepository userRepository,
                             FileStorageService fileStorageService) {
        this.resumeRepository = resumeRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional
    public ResumeResponse uploadResume(Long studentUserId, MultipartFile file) {
        StudentProfile student = studentProfileRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        String savedFileName = fileStorageService.storeFile(file, student.getId());

        Resume resume = new Resume();
        resume.setStudent(student);
        resume.setFileName(file.getOriginalFilename());
        resume.setFilePath(savedFileName);
        
        // If it's the first resume, make it primary
        List<Resume> existingResumes = resumeRepository.findAllByStudentId(student.getId());
        if (existingResumes.isEmpty()) {
            resume.setPrimary(true);
        }

        return mapToResponse(resumeRepository.save(resume));
    }

    @Override
    public List<ResumeResponse> getMyResumes(Long studentUserId) {
        StudentProfile student = studentProfileRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        return resumeRepository.findAllByStudentId(student.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Resource downloadResume(Long resumeId, Long userId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        // Add proper authorization here (e.g. only owner or recruiter can download)
        boolean isOwner = resume.getStudent().getUser().getId().equals(userId);
        boolean isRecruiter = userRepository.findById(userId)
                .map(u -> u.getRole() == Role.RECRUITER || u.getRole() == Role.ADMIN)
                .orElse(false);

        if (!isOwner && !isRecruiter) {
            throw new UnauthorizedException("You are not authorized to download this resume");
        }

        return fileStorageService.loadFileAsResource(resume.getFilePath());
    }

    @Override
    @Transactional
    public void deleteResume(Long studentUserId, Long resumeId) {
        StudentProfile student = studentProfileRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        Resume resume = resumeRepository.findByIdAndStudentId(resumeId, student.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found or does not belong to you"));

        fileStorageService.deleteFile(resume.getFilePath());
        resumeRepository.delete(resume);
    }

    @Override
    @Transactional
    public ResumeResponse setPrimaryResume(Long studentUserId, Long resumeId) {
        StudentProfile student = studentProfileRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        // Unset old primary
        resumeRepository.findByStudentIdAndIsPrimaryTrue(student.getId()).ifPresent(oldPrimary -> {
            oldPrimary.setPrimary(false);
            resumeRepository.save(oldPrimary);
        });

        // Set new primary
        Resume newPrimary = resumeRepository.findByIdAndStudentId(resumeId, student.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
                
        newPrimary.setPrimary(true);
        return mapToResponse(resumeRepository.save(newPrimary));
    }

    private ResumeResponse mapToResponse(Resume resume) {
        ResumeResponse response = new ResumeResponse();
        response.setId(resume.getId());
        response.setStudentId(resume.getStudent().getId());
        response.setFileName(resume.getFileName());
        response.setPrimary(resume.isPrimary());
        response.setUploadedAt(resume.getUploadedAt());
        return response;
    }
}
