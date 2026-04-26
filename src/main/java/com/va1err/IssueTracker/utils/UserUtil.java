package com.va1err.IssueTracker.utils;

import com.va1err.IssueTracker.dto.responses.UserResponse;
import com.va1err.IssueTracker.models.User;

public class UserUtil {

    public static UserResponse toResponse(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .projects(user.getProjects().stream().map(ProjectUtil::toResponse).toList())
                .build();
    }

}
