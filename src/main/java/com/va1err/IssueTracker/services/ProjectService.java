package com.va1err.IssueTracker.services;

import com.va1err.IssueTracker.dto.requests.ProjectRequest;
import com.va1err.IssueTracker.dto.responses.ProjectResponse;
import com.va1err.IssueTracker.enums.Role;
import com.va1err.IssueTracker.exceptions.ProjectNotFoundException;
import com.va1err.IssueTracker.exceptions.UserNotFoundException;
import com.va1err.IssueTracker.models.Project;
import com.va1err.IssueTracker.models.User;
import com.va1err.IssueTracker.repositories.ProjectRepository;
import com.va1err.IssueTracker.repositories.UserRepository;
import com.va1err.IssueTracker.utils.ProjectUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.ArrayList;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public ProjectResponse createProject(ProjectRequest request) {
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new UserNotFoundException(request.getOwnerId()));

        Project project = Project.builder()
                .name(request.getName())
                .owner(owner)
                .issues(new ArrayList<>())
                .build();

        projectRepository.save(project);

        return ProjectUtil.toResponse(project);
    }

    public Page<ProjectResponse> getAllProjects(Pageable pageable) {
        Page<Project> page = projectRepository.findAll(pageable);

        return page.map(ProjectUtil::toResponse);
    }

    public ProjectResponse getProjectById(Long id) {
        return ProjectUtil.toResponse(projectRepository.findById(id).
                orElseThrow(() -> new ProjectNotFoundException(id)));
    }

    public void deleteProjectById(Long id, User currentUser) throws AccessDeniedException {
        if (!projectRepository.existsById(id) || projectRepository.findById(id).isEmpty())
            throw new ProjectNotFoundException(id);

        boolean isOwner = projectRepository.findById(id).get().getOwner().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin)
            throw new AccessDeniedException("Not allowed to delete other user's project");

        projectRepository.deleteById(id);
    }

    public ProjectResponse updateProjectById(Long id, ProjectRequest request, User currentUser) throws AccessDeniedException {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        User owner = userRepository.findById(request.getOwnerId())
                        .orElseThrow(() -> new UserNotFoundException(request.getOwnerId()));

        boolean isOwner = project.getOwner().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin)
            throw new AccessDeniedException("Not allowed to update other user's project");

        project.setName(request.getName());
        project.setOwner(owner);

        projectRepository.save(project);

        return ProjectUtil.toResponse(project);
    }

}
