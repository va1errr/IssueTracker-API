package com.va1err.IssueTracker.services;

import com.va1err.IssueTracker.dto.requests.LoginRequest;
import com.va1err.IssueTracker.dto.requests.RegisterRequest;
import com.va1err.IssueTracker.dto.responses.LoginResponse;
import com.va1err.IssueTracker.dto.responses.UserResponse;
import com.va1err.IssueTracker.enums.Role;
import com.va1err.IssueTracker.exceptions.InvalidPasswordException;
import com.va1err.IssueTracker.exceptions.UserNotFoundException;
import com.va1err.IssueTracker.models.User;
import com.va1err.IssueTracker.repositories.UserRepository;
import com.va1err.IssueTracker.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserResponse register(RegisterRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return UserUtil.toResponse(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new InvalidPasswordException();

        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(token)
                .build();
    }

}
