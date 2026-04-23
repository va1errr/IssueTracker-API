package com.va1err.IssueTracker.controllers;

import com.va1err.IssueTracker.dto.requests.UserRequest;
import com.va1err.IssueTracker.dto.responses.ApiResponse;
import com.va1err.IssueTracker.dto.responses.UserResponse;
import com.va1err.IssueTracker.services.UserService;
import com.va1err.IssueTracker.utils.ApiResponseUtil;
import jakarta.validation.Valid;
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

        return ApiResponseUtil.success(response, "User successfully created");
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers() {
        List<UserResponse> response = userService.getAllUsers();

        return ApiResponseUtil.success(response, "Users fetched successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);

        return ApiResponseUtil.success(response, "User found");
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
