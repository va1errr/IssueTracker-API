package com.va1err.IssueTracker.controllers;

import com.va1err.IssueTracker.dto.requests.ProjectRequest;
import com.va1err.IssueTracker.dto.responses.ApiResponse;
import com.va1err.IssueTracker.dto.responses.ProjectResponse;
import com.va1err.IssueTracker.models.User;
import com.va1err.IssueTracker.services.ProjectService;
import com.va1err.IssueTracker.utils.ApiResponseUtil;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ApiResponse<ProjectResponse> createProject(@RequestBody @Valid ProjectRequest request) {
        ProjectResponse response = projectService.createProject(request);

        return ApiResponseUtil.success(response, "Project created successfully");
    }

    @GetMapping
    public ApiResponse<List<ProjectResponse>> getAllProjects() {
        List<ProjectResponse> response = projectService.getAllProjects();

        return ApiResponseUtil.success(response, "Projects fetched successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<ProjectResponse> getProject(@PathVariable Long id) {
        ProjectResponse response = projectService.getProjectById(id);

        return ApiResponseUtil.success(response, "Project found");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Map<String, Long>> deleteProject(@PathVariable Long id, @AuthenticationPrincipal User user) throws AccessDeniedException {
        projectService.deleteProjectById(id, user);

        Map<String, Long> map = new HashMap<>();
        map.put("id", id);

        return ApiResponseUtil.success(map, "Project found and deleted");
    }

    @PutMapping("/{id}")
    public ApiResponse<ProjectResponse> updateProject(@PathVariable Long id, @RequestBody @Valid ProjectRequest request, @AuthenticationPrincipal User user) throws AccessDeniedException {
        ProjectResponse response = projectService.updateProjectById(id, request, user);

        return ApiResponseUtil.success(response, "Project found and updated");
    }

}
