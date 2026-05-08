package com.va1err.IssueTracker.controllers;

import com.va1err.IssueTracker.dto.requests.LoginRequest;
import com.va1err.IssueTracker.dto.requests.RegisterRequest;
import com.va1err.IssueTracker.dto.responses.ApiResponse;
import com.va1err.IssueTracker.dto.responses.LoginResponse;
import com.va1err.IssueTracker.dto.responses.UserResponse;
import com.va1err.IssueTracker.services.AuthService;
import com.va1err.IssueTracker.utils.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);

        return ApiResponseUtil.success(response, "User registered");
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);

        return ApiResponseUtil.success(response, "User logged in");
    }

}
