package com.va1err.IssueTracker.dto.requests;

import com.va1err.IssueTracker.enums.Priority;
import com.va1err.IssueTracker.enums.Status;
import com.va1err.IssueTracker.models.Project;
import com.va1err.IssueTracker.models.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateIssueRequest {

    @NotBlank
    private String title;

    private String description;

    private Status status;

    private Priority priority;

    private Project project;

    private User owner;

}
