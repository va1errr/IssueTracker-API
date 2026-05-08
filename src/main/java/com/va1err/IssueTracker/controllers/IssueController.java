package com.va1err.IssueTracker.controllers;

import com.va1err.IssueTracker.dto.requests.IssueRequest;
import com.va1err.IssueTracker.dto.responses.ApiResponse;
import com.va1err.IssueTracker.dto.responses.IssueResponse;
import com.va1err.IssueTracker.enums.Priority;
import com.va1err.IssueTracker.enums.Role;
import com.va1err.IssueTracker.enums.Status;
import com.va1err.IssueTracker.services.IssueService;
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
@RequestMapping("/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping
    public ApiResponse<IssueResponse> createIssue(@RequestBody @Valid IssueRequest request) {
        IssueResponse response = issueService.createIssue(request);

        return ApiResponseUtil.success(response, "Issue created successfully");
    }

    @GetMapping
    public ApiResponse<Page<IssueResponse>> getAllIssues(@RequestParam(required = false) Status status, @RequestParam(required = false)Priority priority, @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<IssueResponse> response = issueService.getAllIssues(status, priority, pageable);

        return ApiResponseUtil.success(response, "Issues fetched successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<IssueResponse> getIssue(@PathVariable Long id) {
        IssueResponse response = issueService.getIssueById(id);

        return ApiResponseUtil.success(response, "Issue found");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Map<String, Long>> deleteIssue(@PathVariable Long id) {
        issueService.deleteIssueById(id);

        Map<String, Long> map = new HashMap<>();
        map.put("id", id);

        return ApiResponseUtil.success(map, "Issue found and deleted");
    }

    @PutMapping("/{id}")
    public ApiResponse<IssueResponse> updateIssue(@PathVariable Long id, @RequestBody @Valid IssueRequest request) {
        IssueResponse response = issueService.updateIssueById(id, request);

        return ApiResponseUtil.success(response, "Issue found and updated");
    }

}
