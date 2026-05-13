package com.va1err.IssueTracker.services;

import com.va1err.IssueTracker.dto.requests.UserRequest;
import com.va1err.IssueTracker.dto.responses.IssueResponse;
import com.va1err.IssueTracker.dto.responses.ProjectResponse;
import com.va1err.IssueTracker.dto.responses.UserResponse;
import com.va1err.IssueTracker.enums.Role;
import com.va1err.IssueTracker.enums.Priority;
import com.va1err.IssueTracker.enums.Role;
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
import com.va1err.IssueTracker.utils.ProjectUtil;
import com.va1err.IssueTracker.utils.UserUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final IssueRepository issueRepository;

    public UserService(UserRepository userRepository, ProjectRepository projectRepository, IssueRepository issueRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.issueRepository = issueRepository;
    }

    public UserResponse createUser(UserRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .role(request.getRole())
                .build();

        userRepository.save(user);

        return UserUtil.toResponse(user);
    }

    public Page<UserResponse> getAllUsers(Role role, Pageable pageable) {
        Page<User> page;

        if (role == null)
            page = userRepository.findAll(pageable);
        else
            page = userRepository.findAllByRole(role, pageable);

        return page.map(UserUtil::toResponse);
    }

    public UserResponse getUserById(Long id) {
        return UserUtil.toResponse(userRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException(id)));
    }

    public List<ProjectResponse> getUserProjects(Long id) {
        if (!userRepository.existsById(id))
            throw new UserNotFoundException(id);

        return projectRepository.findAllByOwnerId(id).stream().map(ProjectUtil::toResponse).toList();
    }

    public ProjectResponse getUserProjectById(Long userId, Long projectId) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException(userId);

        Project project = projectRepository.findByOwnerIdAndId(userId, projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        return ProjectUtil.toResponse(project);
    }

    public Page<IssueResponse> getUserProjectIssues(Status status, Priority priority, Pageable pageable, Long userId, Long projectId) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException(userId);

        if (!projectRepository.existsByOwnerIdAndId(userId, projectId))
            throw new ProjectNotFoundException(projectId);

        Page<Issue> page;

        if (status == null && priority == null)
            page = issueRepository.findAllByOwnerIdAndProjectId(pageable, userId, projectId);
        else if (status != null && priority == null)
            page = issueRepository.findAllByOwnerIdAndProjectIdAndStatus(pageable, status, userId, projectId);
        else if (status == null)
            page = issueRepository.findAllByOwnerIdAndProjectIdAndPriority(pageable, priority, userId, projectId);
        else
            page = issueRepository.findAllByOwnerIdAndProjectIdAndStatusAndPriority(pageable, status, priority, userId, projectId);

        return page.map(IssueUtil::toResponse);
    }

    public IssueResponse getUserProjectIssueById(Long userId, Long projectId, Long issueId) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException(userId);

        if (!projectRepository.existsByOwnerIdAndId(userId, projectId))
            throw new ProjectNotFoundException(projectId);

        Issue issue = issueRepository.findByOwnerIdAndProjectIdAndId(userId, projectId, issueId)
                .orElseThrow(() -> new IssueNotFoundException(issueId));

        return IssueUtil.toResponse(issue);
    }

    public void deleteUserById(Long id, User currentUser) throws AccessDeniedException {
        if (!userRepository.existsById(id))
            throw new UserNotFoundException(id);

        boolean isThemself = id.equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isThemself && !isAdmin)
            throw new AccessDeniedException("Not allowed to delete other user");

        userRepository.deleteById(id);
    }

    public UserResponse updateUserById(Long id, UserRequest request, User currentUser) throws AccessDeniedException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        boolean isThemself = id.equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isThemself && !isAdmin)
            throw new AccessDeniedException("Not allowed to update other user");

        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());

        userRepository.save(user);

        return UserUtil.toResponse(user);
    }

}
