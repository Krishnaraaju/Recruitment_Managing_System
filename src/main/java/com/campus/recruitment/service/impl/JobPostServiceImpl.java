package com.campus.recruitment.service.impl;

import com.campus.recruitment.dto.request.JobPostRequest;
import com.campus.recruitment.dto.response.JobPostResponse;
import com.campus.recruitment.entity.Category;
import com.campus.recruitment.entity.JobPost;
import com.campus.recruitment.entity.RecruiterProfile;
import com.campus.recruitment.entity.Skill;
import com.campus.recruitment.exception.BadRequestException;
import com.campus.recruitment.exception.ResourceNotFoundException;
import com.campus.recruitment.exception.UnauthorizedException;
import com.campus.recruitment.repository.CategoryRepository;
import com.campus.recruitment.repository.JobPostRepository;
import com.campus.recruitment.repository.RecruiterProfileRepository;
import com.campus.recruitment.repository.SkillRepository;
import com.campus.recruitment.service.JobPostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JobPostServiceImpl implements JobPostService {

    private final JobPostRepository jobPostRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final CategoryRepository categoryRepository;
    private final SkillRepository skillRepository;

    public JobPostServiceImpl(JobPostRepository jobPostRepository,
                              RecruiterProfileRepository recruiterProfileRepository,
                              CategoryRepository categoryRepository,
                              SkillRepository skillRepository) {
        this.jobPostRepository = jobPostRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.categoryRepository = categoryRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    @Transactional
    public JobPostResponse createJobPost(Long recruiterUserId, JobPostRequest request) {
        RecruiterProfile recruiter = recruiterProfileRepository.findByUserId(recruiterUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found"));
        
        if (!recruiter.isApproved()) {
            throw new UnauthorizedException("Your account must be approved by an Admin to post jobs");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        JobPost jobPost = new JobPost();
        jobPost.setRecruiter(recruiter);
        jobPost.setCompany(recruiter.getCompany());
        jobPost.setCategory(category);
        jobPost.setTitle(request.getTitle());
        jobPost.setType(request.getType());
        jobPost.setLocation(request.getLocation());
        jobPost.setDescription(request.getDescription());
        jobPost.setRequirements(request.getRequirements());
        jobPost.setStipend(request.getStipend());
        jobPost.setDeadline(request.getDeadline());

        setSkillsToJob(jobPost, request.getSkills());

        return mapToResponse(jobPostRepository.save(jobPost));
    }

    @Override
    @Transactional
    public JobPostResponse updateJobPost(Long recruiterUserId, Long jobPostId, JobPostRequest request) {
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new ResourceNotFoundException("Job Post not found"));
                
        // Ensure the recruiter updating is the owner
        if (!jobPost.getRecruiter().getUser().getId().equals(recruiterUserId)) {
            throw new UnauthorizedException("You can only modify your own job posts.");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        jobPost.setCategory(category);
        jobPost.setTitle(request.getTitle());
        jobPost.setType(request.getType());
        jobPost.setLocation(request.getLocation());
        jobPost.setDescription(request.getDescription());
        jobPost.setRequirements(request.getRequirements());
        jobPost.setStipend(request.getStipend());
        jobPost.setDeadline(request.getDeadline());

        setSkillsToJob(jobPost, request.getSkills());

        return mapToResponse(jobPostRepository.save(jobPost));
    }

    private void setSkillsToJob(JobPost jobPost, List<String> skillNames) {
        if (skillNames != null) {
            Set<Skill> skills = new HashSet<>();
            for (String skillName : skillNames) {
                Skill skill = skillRepository.findByName(skillName)
                        .orElseGet(() -> {
                            Skill newSkill = new Skill();
                            newSkill.setName(skillName);
                            return skillRepository.save(newSkill);
                        });
                skills.add(skill);
            }
            jobPost.setSkills(skills);
        }
    }

    @Override
    @Transactional
    public void deleteJobPost(Long recruiterUserId, Long jobPostId) {
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new ResourceNotFoundException("Job Post not found"));
                
        if (!jobPost.getRecruiter().getUser().getId().equals(recruiterUserId)) {
            throw new UnauthorizedException("You can only delete your own job posts.");
        }
        
        jobPost.setDeleted(true);
        jobPostRepository.save(jobPost);
    }

    @Override
    @Transactional
    public void adminDeleteJobPost(Long jobPostId) {
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new ResourceNotFoundException("Job Post not found"));
        jobPost.setDeleted(true);
        jobPostRepository.save(jobPost);
    }

    @Override
    public JobPostResponse getJobPostById(Long jobPostId) {
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new ResourceNotFoundException("Job Post not found"));
        return mapToResponse(jobPost);
    }

    @Override
    public Page<JobPostResponse> getAllActiveJobs(int page, int size, String sortDir, String sortField) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Return non-deleted jobs
        Page<JobPost> jobs = jobPostRepository.findAllByIsDeletedFalse(pageable);
        return jobs.map(this::mapToResponse);
    }

    @Override
    public Page<JobPostResponse> getJobsByRecruiter(Long recruiterUserId, int page, int size) {
        RecruiterProfile recruiter = recruiterProfileRepository.findByUserId(recruiterUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found"));
                
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<JobPost> jobs = jobPostRepository.findAllByRecruiterIdAndIsDeletedFalse(recruiter.getId(), pageable);
        return jobs.map(this::mapToResponse);
    }

    private JobPostResponse mapToResponse(JobPost jobPost) {
        JobPostResponse response = new JobPostResponse();
        response.setId(jobPost.getId());
        response.setRecruiterId(jobPost.getRecruiter().getId());
        response.setCompanyName(jobPost.getCompany().getName());
        response.setCategoryName(jobPost.getCategory().getName());
        response.setTitle(jobPost.getTitle());
        response.setType(jobPost.getType());
        response.setLocation(jobPost.getLocation());
        response.setDescription(jobPost.getDescription());
        response.setRequirements(jobPost.getRequirements());
        response.setStipend(jobPost.getStipend());
        response.setDeadline(jobPost.getDeadline());
        response.setStatus(jobPost.getStatus());
        response.setCreatedAt(jobPost.getCreatedAt());
        
        List<String> skillNames = jobPost.getSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.toList());
        response.setSkills(skillNames);
        
        return response;
    }
}
