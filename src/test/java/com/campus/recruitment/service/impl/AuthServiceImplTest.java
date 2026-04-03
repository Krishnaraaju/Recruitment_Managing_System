package com.campus.recruitment.service.impl;

import com.campus.recruitment.dto.request.RegisterRequest;
import com.campus.recruitment.entity.User;
import com.campus.recruitment.entity.enums.Role;
import com.campus.recruitment.exception.BadRequestException;
import com.campus.recruitment.repository.CompanyRepository;
import com.campus.recruitment.repository.RecruiterProfileRepository;
import com.campus.recruitment.repository.StudentProfileRepository;
import com.campus.recruitment.repository.UserRepository;
import com.campus.recruitment.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StudentProfileRepository studentProfileRepository;
    @Mock
    private RecruiterProfileRepository recruiterProfileRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void register_WhenEmailExists_ShouldThrowBadRequestException() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@test.com");
        
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_WhenValidStudent_ShouldSaveUserAndProfile() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("student@test.com");
        request.setPassword("password");
        request.setRole(Role.STUDENT);
        request.setFullName("John Doe");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        
        User savedUser = new User();
        savedUser.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        authService.register(request);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
        verify(studentProfileRepository, times(1)).save(any());
        verify(recruiterProfileRepository, never()).save(any());
    }
}
