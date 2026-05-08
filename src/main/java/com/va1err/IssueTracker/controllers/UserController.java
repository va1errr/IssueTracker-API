package com.va1err.IssueTracker.controllers;

import com.va1err.IssueTracker.dto.requests.UserRequest;
import com.va1err.IssueTracker.dto.responses.ApiResponse;
import com.va1err.IssueTracker.dto.responses.IssueResponse;
import com.va1err.IssueTracker.dto.responses.ProjectResponse;
import com.va1err.IssueTracker.dto.responses.UserResponse;
import com.va1err.IssueTracker.enums.Priority;
import com.va1err.IssueTracker.enums.Role;
import com.va1err.IssueTracker.enums.Status;
import com.va1err.IssueTracker.services.UserService;
import com.va1err.IssueTracker.utils.ApiResponseUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserRequest request) {
        UserResponse response = userService.createUser(request);

        return ApiResponseUtil.success(response, "User created successfully");
    }

    @GetMapping
    public ApiResponse<Page<UserResponse>> getAllUsers(@RequestParam(required = false) Role role, @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<UserResponse> response = userService.getAllUsers(role, pageable);

        return ApiResponseUtil.success(response, "Users fetched successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);

        return ApiResponseUtil.success(response, "User found");
    }

    @GetMapping("/{id}/projects")
    public ApiResponse<List<ProjectResponse>> getUserProjects(@PathVariable Long id) {
        List<ProjectResponse> response = userService.getUserProjects(id);

        return ApiResponseUtil.success(response, "User projects fetched successfully");
    }

    @GetMapping("/{userId}/projects/{projectId}")
    public ApiResponse<ProjectResponse> getUserProject(@PathVariable Long userId, @PathVariable Long projectId) {
        ProjectResponse response = userService.getUserProjectById(userId, projectId);

        return ApiResponseUtil.success(response, "User project found");
    }

    @GetMapping("/{userId}/projects/{projectId}/issues")
    public ApiResponse<Page<IssueResponse>> getUserProjectIssues(@RequestParam(required = false) Status status, @RequestParam(required = false) Priority priority, @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable, @PathVariable Long userId, @PathVariable Long projectId) {
        Page<IssueResponse> response = userService.getUserProjectIssues(status, priority, pageable, userId, projectId);

        return ApiResponseUtil.success(response, "User project issues fetched successfully");
    }

    @GetMapping("/{userId}/projects/{projectId}/issues/{issueId}")
    public ApiResponse<IssueResponse> getUserProjectIssue(@PathVariable Long userId, @PathVariable Long projectId, @PathVariable Long issueId) {
        IssueResponse response = userService.getUserProjectIssueById(userId, projectId, issueId);

        return ApiResponseUtil.success(response, "User project issue found");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Map<String, Long>> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);

        Map<String, Long> map = new HashMap<>();
        map.put("id", id);

        return ApiResponseUtil.success(map, "User found and deleted");
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable Long id, @RequestBody @Valid UserRequest request) {
        UserResponse response = userService.updateUserById(id, request);

        return ApiResponseUtil.success(response, "User found and updated");
    }

}
