package com.campus.recruitment.service.impl;

import com.campus.recruitment.dto.request.LoginRequest;
import com.campus.recruitment.dto.request.RegisterRequest;
import com.campus.recruitment.dto.response.AuthResponse;
import com.campus.recruitment.entity.Company;
import com.campus.recruitment.entity.RecruiterProfile;
import com.campus.recruitment.entity.StudentProfile;
import com.campus.recruitment.entity.User;
import com.campus.recruitment.entity.enums.Role;
import com.campus.recruitment.exception.BadRequestException;
import com.campus.recruitment.repository.CompanyRepository;
import com.campus.recruitment.repository.RecruiterProfileRepository;
import com.campus.recruitment.repository.StudentProfileRepository;
import com.campus.recruitment.repository.UserRepository;
import com.campus.recruitment.security.CustomUserDetails;
import com.campus.recruitment.security.JwtUtil;
import com.campus.recruitment.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${app.jwt.expiration-ms}")
    private Long jwtExpirationMs;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository,
                           StudentProfileRepository studentProfileRepository,
                           RecruiterProfileRepository recruiterProfileRepository,
                           CompanyRepository companyRepository,
                           PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        return AuthResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .role(user.getRole())
                .expiresIn(jwtExpirationMs / 1000)
                .userId(user.getId())
                .build();
    }

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        
        user = userRepository.save(user);

        // Create specific profile based on role
        if (request.getRole() == Role.STUDENT) {
            StudentProfile profile = new StudentProfile();
            profile.setUser(user);
            profile.setFullName(request.getFullName());
            profile.setPhone(request.getPhone());
            profile.setCollege(request.getCollege());
            profile.setDegree(request.getDegree());
            profile.setBranch(request.getBranch());
            profile.setGraduationYear(request.getGraduationYear());
            studentProfileRepository.save(profile);

        } else if (request.getRole() == Role.RECRUITER) {
            RecruiterProfile profile = new RecruiterProfile();
            profile.setUser(user);
            profile.setFullName(request.getFullName());
            profile.setDesignation(request.getDesignation());

            if (request.getCompanyId() != null) {
                Company company = companyRepository.findById(request.getCompanyId())
                        .orElseThrow(() -> new BadRequestException("Company not found"));
                profile.setCompany(company);
            } else {
                throw new BadRequestException("Company ID is required for recruiters");
            }
            // Admin must approve recruiters before they can post jobs
            profile.setApproved(false); 
            recruiterProfileRepository.save(profile);
            
        } else if (request.getRole() == Role.ADMIN) {
            // Admin profiles can be registered, handled directly
        }
    }
}
