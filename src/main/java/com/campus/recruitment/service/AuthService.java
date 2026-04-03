package com.campus.recruitment.service;

import com.campus.recruitment.dto.request.LoginRequest;
import com.campus.recruitment.dto.request.RegisterRequest;
import com.campus.recruitment.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    void register(RegisterRequest registerRequest);
}
