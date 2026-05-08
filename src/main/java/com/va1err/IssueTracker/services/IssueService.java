package com.va1err.IssueTracker.services;

import com.va1err.IssueTracker.dto.requests.IssueRequest;
import com.va1err.IssueTracker.dto.responses.IssueResponse;
import com.va1err.IssueTracker.enums.Priority;
import com.va1err.IssueTracker.enums.Status;
import com.va1err.IssueTracker.exceptions.IssueNotFoundException;
import com.va1err.IssueTracker.exceptions.ProjectNotFoundException;
import com.va1err.IssueTracker.exceptions.UserNotFoundException;
import com.va1err.IssueTracker.models.Issue;
import com.va1err.IssueTracker.models.Project;
import com.va1err.IssueTracker.models.User;
import com.va1err.IssueTracker.repositories.IssueRepository;
import com.va1err.IssueTracker.repositories.ProjectRepository;
import com.va1err.IssueTracker.repositories.UserRepository;
import com.va1err.IssueTracker.utils.IssueUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public IssueService(IssueRepository issueRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.issueRepository = issueRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public IssueResponse createIssue(IssueRequest request) {
        User user = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new UserNotFoundException(request.getOwnerId()));

        Project project = projectRepository.findByOwnerIdAndId(user.getId(), request.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(request.getProjectId()));

        Issue issue = Issue.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .priority(request.getPriority())
                .project(project)
                .owner(user)
                .build();

        issueRepository.save(issue);

        return IssueUtil.toResponse(issue);
    }

    public Page<IssueResponse> getAllIssues(Status status, Priority priority, Pageable pageable) {
        Page<Issue> page;

        if (status == null && priority == null)
            page = issueRepository.findAll(pageable);
        else if (status != null && priority == null)
            page = issueRepository.findAllByStatus(pageable, status);
        else if (status == null)
            page = issueRepository.findAllByPriority(pageable, priority);
        else page = issueRepository.findAllByStatusAndPriority(pageable, status, priority);

        return page.map(IssueUtil::toResponse);
    }

    public IssueResponse getIssueById(Long id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new IssueNotFoundException(id));

        return IssueUtil.toResponse(issue);
    }

    public void deleteIssueById(Long id) {
        if (!issueRepository.existsById(id))
            throw new IssueNotFoundException(id);

        issueRepository.deleteById(id);
    }

    public IssueResponse updateIssueById(Long id, IssueRequest request) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new IssueNotFoundException(id));

        User user = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new UserNotFoundException(request.getOwnerId()));

        Project project = projectRepository.findByOwnerIdAndId(user.getId(), request.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(request.getProjectId()));

        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setStatus(request.getStatus());
        issue.setPriority(request.getPriority());
        issue.setProject(project);
        issue.setOwner(user);

        issueRepository.save(issue);

        return IssueUtil.toResponse(issue);
    }

}
