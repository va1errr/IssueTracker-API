package com.va1err.IssueTracker.utils;

import com.va1err.IssueTracker.dto.responses.UserResponse;
import com.va1err.IssueTracker.models.User;

import java.util.ArrayList;

public class UserUtil {

    public static UserResponse toResponse(User user) {
        if (user == null) return null;

        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();

        if (user.getProjects() == null)
            response.setProjects(new ArrayList<>());
        else
            response.setProjects(user.getProjects().stream().map(ProjectUtil::toResponse).toList());

        return response;
    }

}
