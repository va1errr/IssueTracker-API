package com.va1err.IssueTracker.dto.responses;

import com.va1err.IssueTracker.models.Issue;
import com.va1err.IssueTracker.models.User;
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
public class ProjectResponse {

    private Long id;

    private String name;

    private User owner;

    private LocalDateTime createdAt;

    private List<Issue> issues;

}
