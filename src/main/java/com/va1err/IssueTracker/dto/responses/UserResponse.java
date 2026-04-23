package com.va1err.IssueTracker.dto.responses;

import com.va1err.IssueTracker.enums.Role;
import com.va1err.IssueTracker.models.Issue;
import com.va1err.IssueTracker.models.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;

    private String email;

    private String username;

    private String password;

    private Role role;

    private LocalDateTime createdAt;

    private List<Project> projects;

    private List<Issue> issues;

}
