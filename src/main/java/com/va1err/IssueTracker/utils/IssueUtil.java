package com.va1err.IssueTracker.utils;

import com.va1err.IssueTracker.dto.responses.IssueResponse;
import com.va1err.IssueTracker.models.Issue;

public class IssueUtil {

    public static IssueResponse toResponse(Issue issue) {
        if (issue == null) return null;

        return IssueResponse.builder()
                .id(issue.getId())
                .title(issue.getTitle())
                .description(issue.getDescription())
                .status(issue.getStatus())
                .priority(issue.getPriority())
                .projectId(issue.getProject().getId())
                .ownerId(issue.getOwner().getId())
                .createdAt(issue.getCreatedAt())
                .build();
    }

}
