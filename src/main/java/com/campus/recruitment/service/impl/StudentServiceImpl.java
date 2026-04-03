package com.campus.recruitment.service.impl;

import com.campus.recruitment.dto.request.StudentProfileRequest;
import com.campus.recruitment.dto.response.StudentProfileResponse;
import com.campus.recruitment.entity.Skill;
import com.campus.recruitment.entity.StudentProfile;
import com.campus.recruitment.exception.ResourceNotFoundException;
import com.campus.recruitment.repository.SkillRepository;
import com.campus.recruitment.repository.StudentProfileRepository;
import com.campus.recruitment.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentProfileRepository studentProfileRepository;
    private final SkillRepository skillRepository;

    public StudentServiceImpl(StudentProfileRepository studentProfileRepository, SkillRepository skillRepository) {
        this.studentProfileRepository = studentProfileRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    public StudentProfileResponse getProfile(Long userId) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user ID: " + userId));
        return mapToResponse(profile);
    }

    @Override
    @Transactional
    public StudentProfileResponse updateProfile(Long userId, StudentProfileRequest request) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found setup."));

        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setCollege(request.getCollege());
        profile.setDegree(request.getDegree());
        profile.setBranch(request.getBranch());
        profile.setGraduationYear(request.getGraduationYear());
        profile.setCgpa(request.getCgpa());
        profile.setBio(request.getBio());

        if (request.getSkills() != null) {
            Set<Skill> skills = new HashSet<>();
            for (String skillName : request.getSkills()) {
                Skill skill = skillRepository.findByName(skillName)
                        .orElseGet(() -> {
                            Skill newSkill = new Skill();
                            newSkill.setName(skillName);
                            return skillRepository.save(newSkill);
                        });
                skills.add(skill);
            }
            profile.setSkills(skills);
        }

        StudentProfile updatedProfile = studentProfileRepository.save(profile);
        return mapToResponse(updatedProfile);
    }

    @Override
    public StudentProfileResponse getProfileById(Long profileId) {
        StudentProfile profile = studentProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        return mapToResponse(profile);
    }

    private StudentProfileResponse mapToResponse(StudentProfile profile) {
        StudentProfileResponse response = new StudentProfileResponse();
        response.setId(profile.getId());
        response.setUserId(profile.getUser().getId());
        response.setFullName(profile.getFullName());
        response.setPhone(profile.getPhone());
        response.setCollege(profile.getCollege());
        response.setDegree(profile.getDegree());
        response.setBranch(profile.getBranch());
        response.setGraduationYear(profile.getGraduationYear());
        response.setCgpa(profile.getCgpa());
        response.setBio(profile.getBio());
        
        List<String> skillNames = profile.getSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.toList());
        response.setSkills(skillNames);
        
        return response;
    }
}
