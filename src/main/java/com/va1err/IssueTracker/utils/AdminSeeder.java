package com.va1err.IssueTracker.utils;

import com.va1err.IssueTracker.enums.Role;
import com.va1err.IssueTracker.models.User;
import com.va1err.IssueTracker.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String email;

    @Value("${admin.username}")
    private String username;

    @Value("${admin.password}")
    private String password;


    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail(email).isPresent())
            return;

        userRepository.save(User.builder()
                .email(email)
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(Role.ADMIN)
                .build()
        );
    }
}
