package com.va1err.IssueTracker.utils;

import com.va1err.IssueTracker.dto.responses.ProjectResponse;
import com.va1err.IssueTracker.models.Project;

import java.util.ArrayList;

public class ProjectUtil {

    public static ProjectResponse toResponse(Project project) {
        if (project == null) return null;

        ProjectResponse response = ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .ownerId(project.getOwner().getId())
                .createdAt(project.getCreatedAt())
                .build();

        if (project.getIssues() == null)
            response.setIssues(new ArrayList<>());
        else
            response.setIssues(project.getIssues().stream().map(IssueUtil::toResponse).toList());

        return response;
    }

}
