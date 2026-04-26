package com.va1err.IssueTracker.services;

import com.va1err.IssueTracker.dto.requests.ProjectRequest;
import com.va1err.IssueTracker.dto.responses.ProjectResponse;
import com.va1err.IssueTracker.exceptions.ProjectNotFoundException;
import com.va1err.IssueTracker.exceptions.UserNotFoundException;
import com.va1err.IssueTracker.models.Project;
import com.va1err.IssueTracker.models.User;
import com.va1err.IssueTracker.repositories.ProjectRepository;
import com.va1err.IssueTracker.repositories.UserRepository;
import com.va1err.IssueTracker.utils.ProjectUtil;
import org.springframework.stereotype.Service;

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

    public List<ProjectResponse> getAllProjects() {
        List<Project> projects = projectRepository.findAll();

        return projects.stream().map(ProjectUtil::toResponse).toList();
    }

    public ProjectResponse getProjectById(Long id) {
        return ProjectUtil.toResponse(projectRepository.findById(id).
                orElseThrow(() -> new ProjectNotFoundException(id)));
    }

    public void deleteProjectById(Long id) {
        if (!projectRepository.existsById(id))
            throw new ProjectNotFoundException(id);

        projectRepository.deleteById(id);
    }

    public ProjectResponse updateProjectById(Long id, ProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        User owner = userRepository.findById(request.getOwnerId())
                        .orElseThrow(() -> new UserNotFoundException(request.getOwnerId()));

        project.setName(request.getName());
        project.setOwner(owner);

        projectRepository.save(project);

        return ProjectUtil.toResponse(project);
    }

}
