package com.va1err.IssueTracker.dto.responses;

import com.va1err.IssueTracker.enums.Priority;
import com.va1err.IssueTracker.enums.Status;
import com.va1err.IssueTracker.models.Project;
import com.va1err.IssueTracker.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueResponse {

    private Long id;

    private String title;

    private String description;

    private Status status;

    private Priority priority;

    private Project project;

    private User owner;

    private LocalDateTime createdAt;

}
