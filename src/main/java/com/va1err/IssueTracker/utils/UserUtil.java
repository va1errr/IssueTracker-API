package com.va1err.IssueTracker.utils;

import com.va1err.IssueTracker.dto.responses.UserResponse;
import com.va1err.IssueTracker.models.User;

public class UserUtil {

    public static User toUser(UserResponse response) {
        if  (response == null) return null;

        return User.builder()
                .id(response.getId())
                .email(response.getEmail())
                .username(response.getUsername())
                .password(response.getPassword())
                .role(response.getRole())
                .createdAt(response.getCreatedAt())
                .projects(response.getProjects())
                .issues(response.getIssues())
                .build();
    }

    public static UserResponse toResponse(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .projects(user.getProjects())
                .issues(user.getIssues())
                .build();
    }

}
