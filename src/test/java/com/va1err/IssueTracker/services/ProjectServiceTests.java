package com.va1err.IssueTracker.services;

import com.va1err.IssueTracker.dto.requests.ProjectRequest;
import com.va1err.IssueTracker.dto.responses.ProjectResponse;
import com.va1err.IssueTracker.exceptions.ProjectNotFoundException;
import com.va1err.IssueTracker.exceptions.UserNotFoundException;
import com.va1err.IssueTracker.models.Project;
import com.va1err.IssueTracker.models.User;
import com.va1err.IssueTracker.repositories.ProjectRepository;
import com.va1err.IssueTracker.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTests {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    public void createProject_shouldReturnProject_whenFound() {
        User user = User.builder()
                .id(1L)
                .email("test@email.com")
                .username("test")
                .password("password")
                .build();

        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project project = invocation.getArgument(0);
            project.setId(1L);
            return project;
        });

        ProjectRequest request = ProjectRequest.builder()
                .name("test")
                .ownerId(1L)
                .build();

        ProjectResponse response = projectService.createProject(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("test", response.getName());
        assertEquals(1L, response.getOwnerId());
        assertThat(response.getIssues().isEmpty());

        verify(userRepository).findById(eq(1L));
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    public void createProject_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.empty());

        ProjectRequest request = ProjectRequest.builder()
                .name("test")
                .ownerId(1L)
                .build();

        assertThrows(UserNotFoundException.class, () -> projectService.createProject(request));

        verify(userRepository).findById(eq(1L));
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    public void getAllProjects_shouldReturnProjects() {
        User user = User.builder()
                .id(1L)
                .email("test@email.com")
                .username("test")
                .password("password")
                .build();

        Project project1 = Project.builder()
                .id(1L)
                .name("test1")
                .owner(user)
                .build();

        Project project2 = Project.builder()
                .id(2L)
                .name("test2")
                .owner(user)
                .build();

        List<Project> projects = Arrays.asList(project1, project2);

        when(projectRepository.findAll()).thenReturn(projects);

        List<ProjectResponse> response = projectService.getAllProjects();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.getFirst().getId());
        assertEquals("test1", response.getFirst().getName());
        assertEquals(1L, response.getFirst().getOwnerId());
        assertEquals(2L, response.get(1).getId());
        assertEquals("test2", response.get(1).getName());
        assertEquals(1L, response.get(1).getOwnerId());

        verify(projectRepository).findAll();
    }

    @Test
    public void getProjectById_shouldReturnProject_whenFound() {
        User user = User.builder()
                .id(1L)
                .email("test@email.com")
                .username("test")
                .password("password")
                .build();

        Project project = Project.builder()
                .id(1L)
                .name("test")
                .owner(user)
                .build();

        when(projectRepository.findById(eq(1L))).thenReturn(Optional.of(project));

        ProjectResponse response = projectService.getProjectById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("test", response.getName());
        assertEquals(1L, response.getOwnerId());

        verify(projectRepository).findById(eq(1L));
    }

    @Test
    public void getProjectById_shouldThrowException_whenProjectNotFound() {
        when(projectRepository.findById(eq(1L))).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectById(1L));

        verify(projectRepository).findById(eq(1L));
    }

    @Test
    public void deleteProjectById_shouldDeleteProject_whenFound() {
        when(projectRepository.existsById(eq(1L))).thenReturn(true);

        projectService.deleteProjectById(1L);

        verify(projectRepository).existsById(eq(1L));
        verify(projectRepository).deleteById(eq(1L));
    }

    @Test
    public void deleteProjectById_shouldThrowException_whenProjectNotFound() {
        when(projectRepository.existsById(eq(1L))).thenReturn(false);

        assertThrows(ProjectNotFoundException.class, () -> projectService.deleteProjectById(1L));

        verify(projectRepository).existsById(eq(1L));
        verify(projectRepository, never()).deleteById(any());
    }

    @Test
    public void updateProjectById_shouldUpdateAndReturnProject_whenFound() {
        User user1 = User.builder()
                .id(1L)
                .email("test1@email.com")
                .username("test1")
                .password("password1")
                .build();

        User user2 = User.builder()
                .id(2L)
                .email("test2@email.com")
                .username("test2")
                .password("password2")
                .build();

        Project project = Project.builder()
                .id(1L)
                .name("test")
                .owner(user1)
                .build();

        when(projectRepository.findById(eq(1L))).thenReturn(Optional.of(project));
        when(userRepository.findById(eq(2L))).thenReturn(Optional.of(user2));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project pr = invocation.getArgument(0);
            pr.setId(1L);
            return pr;
        });

        ProjectRequest request = ProjectRequest.builder()
                .name("update name")
                .ownerId(2L)
                .build();

        ProjectResponse response = projectService.updateProjectById(1L, request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("update name", response.getName());
        assertEquals(2L, response.getOwnerId());

        verify(projectRepository).findById(eq(1L));
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    public void updateProjectById_shouldThrowException_whenProjectNotFound() {
        when(projectRepository.findById(eq(1L))).thenReturn(Optional.empty());

        ProjectRequest request = ProjectRequest.builder()
                .name("test")
                .ownerId(1L)
                .build();

        assertThrows(ProjectNotFoundException.class, () -> projectService.updateProjectById(1L, request));

        verify(projectRepository).findById(eq(1L));
        verify(userRepository, never()).findById(any());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    public void updateProjectById_shouldThrowException_whenUserNotFound() {
        User user = User.builder()
                .id(1L)
                .email("test@email.com")
                .username("test")
                .password("password")
                .build();

        Project project = Project.builder()
                .id(1L)
                .name("test")
                .owner(user)
                .build();

        when(projectRepository.findById(eq(1L))).thenReturn(Optional.of(project));
        when(userRepository.findById(eq(2L))).thenReturn(Optional.empty());

        ProjectRequest request = ProjectRequest.builder()
                .name("test")
                .ownerId(2L)
                .build();

        assertThrows(UserNotFoundException.class, () -> projectService.updateProjectById(1L, request));

        verify(projectRepository).findById(eq(1L));
        verify(userRepository).findById(eq(2L));
        verify(projectRepository, never()).save(any(Project.class));
    }

}
