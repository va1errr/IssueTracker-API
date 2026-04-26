package com.va1err.IssueTracker.utils;

import com.va1err.IssueTracker.dto.responses.ProjectResponse;
import com.va1err.IssueTracker.models.Project;

public class ProjectUtil {

    public static ProjectResponse toResponse(Project project) {
        if (project == null) return null;

        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .ownerId(project.getOwner().getId())
                .createdAt(project.getCreatedAt())
                .issues(project.getIssues().stream().map(IssueUtil::toResponse).toList())
                .build();
    }

}
