package com.va1err.IssueTracker.services;

import com.va1err.IssueTracker.dto.requests.UserRequest;
import com.va1err.IssueTracker.dto.responses.IssueResponse;
import com.va1err.IssueTracker.dto.responses.ProjectResponse;
import com.va1err.IssueTracker.dto.responses.UserResponse;
import com.va1err.IssueTracker.enums.Role;
import com.va1err.IssueTracker.exceptions.IssueNotFoundException;
import com.va1err.IssueTracker.exceptions.ProjectNotFoundException;
import com.va1err.IssueTracker.exceptions.UserNotFoundException;
import com.va1err.IssueTracker.models.Issue;
import com.va1err.IssueTracker.models.Project;
import com.va1err.IssueTracker.models.User;
import com.va1err.IssueTracker.repositories.IssueRepository;
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
public class UserServiceTests {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private IssueRepository issueRepository;

    @Test
    public void createUser_shouldSaveAndReturnResponse() {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
                }
        );

        UserRequest request = UserRequest.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .role(Role.USER)
                .build();

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals("test@email", response.getEmail());
        assertEquals("test", response.getUsername());
        assertEquals(Role.USER, response.getRole());
        assertThat(response.getProjects().isEmpty());

        verify(userRepository).save(any(User.class));
    }

    @Test
    public void getAllUsers_shouldReturnUsers() {
        User user1 = User.builder()
                .id(1L)
                .email("test1@email")
                .username("test1")
                .password("password1")
                .role(Role.USER)
                .build();

        User user2 = User.builder()
                .id(2L)
                .email("test2@email")
                .username("test2")
                .password("password2")
                .role(Role.ADMIN)
                .build();

        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        List<UserResponse> response = userService.getAllUsers();

        assertNotNull(response);
        assertEquals("test1@email", response.getFirst().getEmail());
        assertEquals("test1", response.getFirst().getUsername());
        assertEquals(Role.USER, response.getFirst().getRole());
        assertEquals("test2@email", response.get(1).getEmail());
        assertEquals("test2", response.get(1).getUsername());
        assertEquals(Role.ADMIN, response.get(1).getRole());

        verify(userRepository).findAll();
    }

    @Test
    public void getUserById_shouldReturnUser_whenUserFound() {
        User user = User.builder()
                .id(1L)
                .email("test@email")
                .username("test")
                .password("password")
                .role(Role.USER)
                .build();

        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals("test@email", response.getEmail());
        assertEquals("test", response.getUsername());
        assertEquals(Role.USER, response.getRole());

        verify(userRepository).findById(eq(1L));
    }

    @Test
    public void getUserById_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));

        verify(userRepository).findById(eq(1L));
    }

    @Test
    public void getUserProjects_shouldReturnProjects_whenUserFound() {
        User user = User.builder()
                .id(1L)
                .email("test@email")
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

        user.setProjects(projects);

        when(userRepository.existsById(eq(1L))).thenReturn(true);
        when(projectRepository.findAllByOwnerId(eq(1L))).thenReturn(projects);

        List<ProjectResponse> response = userService.getUserProjects(1L);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.getFirst().getOwnerId());
        assertEquals(1L, response.get(1).getOwnerId());

        verify(userRepository).existsById(eq(1L));
        verify(projectRepository).findAllByOwnerId(eq(1L));
    }

    @Test
    public void getUserProjects_shouldThrowException_whenUserNotFound() {
        when(userRepository.existsById(eq(1L))).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.getUserProjects(1L));

        verify(userRepository).existsById(eq(1L));
        verify(projectRepository, never()).findAllByOwnerId(any());
    }

    @Test
    public void getUserProjectById_shouldReturnProject_whenFound() {
        User user = User.builder()
                .id(1L)
                .email("test@email")
                .username("test")
                .password("password")
                .build();

        Project project = Project.builder()
                .id(1L)
                .name("test")
                .owner(user)
                .build();

        when(userRepository.existsById(eq(1L))).thenReturn(true);
        when(projectRepository.findByOwnerIdAndId(eq(1L), eq(1L))).thenReturn(Optional.of(project));

        ProjectResponse response = userService.getUserProjectById(1L, 1L);

        assertNotNull(response);
        assertEquals("test", response.getName());
        assertEquals(1L, response.getOwnerId());

        verify(userRepository).existsById(eq(1L));
        verify(projectRepository).findByOwnerIdAndId(eq(1L), eq(1L));
    }

    @Test
    public void getUserProjectById_shouldThrowException_whenUserNotFound() {
        when(userRepository.existsById(eq(1L))).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.getUserProjectById(1L, 1L));

        verify(userRepository).existsById(eq(1L));
        verify(projectRepository, never()).findByOwnerIdAndId(any(), any());
    }

    @Test
    public void getUserProjectById_shouldThrowException_whenUserProjectNotFound() {
        when(userRepository.existsById(eq(1L))).thenReturn(true);

        assertThrows(ProjectNotFoundException.class, () -> userService.getUserProjectById(1L, 1L));

        verify(userRepository).existsById(eq(1L));
        verify(projectRepository).findByOwnerIdAndId(eq(1L), eq(1L));
    }

    @Test
    public void getUserProjectIssues_shouldReturnIssues_whenFound() {
        User user = User.builder()
                .id(1L)
                .email("test@email")
                .username("test")
                .password("password")
                .build();

        Project project = Project.builder()
                .id(1L)
                .name("test")
                .owner(user)
                .build();

        Issue issue1 = Issue.builder()
                .id(1L)
                .title("test1")
                .project(project)
                .owner(user)
                .build();

        Issue issue2 = Issue.builder()
                .id(2L)
                .title("test2")
                .project(project)
                .owner(user)
                .build();

        List<Issue> issues = Arrays.asList(issue1, issue2);

        when(userRepository.existsById(eq(1L))).thenReturn(true);
        when(projectRepository.existsByOwnerIdAndId(eq(1L), eq(1L))).thenReturn(true);
        when(issueRepository.findAllByOwnerIdAndProjectId(eq(1L), eq(1L))).thenReturn(issues);

        List<IssueResponse> response = userService.getUserProjectIssues(1L, 1L);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.getFirst().getProjectId());
        assertEquals(1L, response.getFirst().getOwnerId());
        assertEquals(1L, response.get(1).getProjectId());
        assertEquals(1L, response.get(1).getOwnerId());

        verify(userRepository).existsById(eq(1L));
        verify(projectRepository).existsByOwnerIdAndId(eq(1L), eq(1L));
        verify(issueRepository).findAllByOwnerIdAndProjectId(eq(1L), eq(1L));
    }

    @Test
    public void getUserProjectIssues_shouldThrowException_whenUserNotFound() {
        when(userRepository.existsById(eq(1L))).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.getUserProjectIssues(1L, 1L));

        verify(userRepository).existsById(eq(1L));
        verify(projectRepository, never()).existsByOwnerIdAndId(any(), any());
        verify(issueRepository, never()).findAllByOwnerIdAndProjectId(any(), any());
    }

    @Test
    public void getUserProjectIssues_shouldThrowException_whenProjectNotFound() {
        when(userRepository.existsById(eq(1L))).thenReturn(true);
        when(projectRepository.existsByOwnerIdAndId(eq(1L), eq(1L))).thenReturn(false);

        assertThrows(ProjectNotFoundException.class, () -> userService.getUserProjectIssues(1L, 1L));

        verify(userRepository).existsById(eq(1L));
        verify(projectRepository).existsByOwnerIdAndId(eq(1L), eq(1L));
        verify(issueRepository, never()).findAllByOwnerIdAndProjectId(any(), any());
    }

    @Test
    public void getUserProjectIssueById_shouldReturnIssue_whenFound() {
        User user = User.builder()
                .id(1L)
                .email("test@email")
                .username("test")
                .password("password")
                .build();

        Project project = Project.builder()
                .id(1L)
                .name("test")
                .owner(user)
                .build();

        Issue issue = Issue.builder()
                .id(1L)
                .title("test")
                .project(project)
                .owner(user)
                .build();

        when(userRepository.existsById(eq(1L))).thenReturn(true);
        when(projectRepository.existsByOwnerIdAndId(eq(1L), eq(1L))).thenReturn(true);
        when(issueRepository.findByOwnerIdAndProjectIdAndId(eq(1L), eq(1L), eq(1L))).thenReturn(Optional.of(issue));

        IssueResponse response = userService.getUserProjectIssueById(1L, 1L, 1L);

        assertNotNull(response);
        assertEquals("test", response.getTitle());
        assertEquals(1L, response.getProjectId());
        assertEquals(1L, response.getOwnerId());

        verify(userRepository).existsById(eq(1L));
        verify(projectRepository).existsByOwnerIdAndId(eq(1L), eq(1L));
        verify(issueRepository).findByOwnerIdAndProjectIdAndId(eq(1L), eq(1L), eq(1L));
    }

    @Test
    public void getUserProjectIssueById_shouldThrowException_whenUserNotFound() {
        when(userRepository.existsById(eq(1L))).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.getUserProjectIssueById(1L, 1L, 1L));

        verify(userRepository).existsById(eq(1L));
        verify(projectRepository, never()).existsByOwnerIdAndId(any(), any());
        verify(issueRepository, never()).findByOwnerIdAndProjectIdAndId(any(), any(), any());
    }

    @Test
    public void getUserProjectIssueById_shouldThrowException_whenUserProjectNotFound() {
        when(userRepository.existsById(eq(1L))).thenReturn(true);
        when(projectRepository.existsByOwnerIdAndId(eq(1L), eq(1L))).thenReturn(false);

        assertThrows(ProjectNotFoundException.class, () -> userService.getUserProjectIssueById(1L, 1L, 1L));

        verify(userRepository).existsById(eq(1L));
        verify(projectRepository).existsByOwnerIdAndId(eq(1L), eq(1L));
        verify(issueRepository, never()).findByOwnerIdAndProjectIdAndId(any(), any(), any());
    }

    @Test
    public void getUserProjectIssueById_shouldThrowException_whenIssueNotFound() {
        when(userRepository.existsById(eq(1L))).thenReturn(true);
        when(projectRepository.existsByOwnerIdAndId(eq(1L), eq(1L))).thenReturn(true);
        when(issueRepository.findByOwnerIdAndProjectIdAndId(eq(1L), eq(1L), eq(1L))).thenReturn(Optional.empty());

        assertThrows(IssueNotFoundException.class, () -> userService.getUserProjectIssueById(1L, 1L, 1L));

        verify(userRepository).existsById(eq(1L));
        verify(projectRepository).existsByOwnerIdAndId(eq(1L), eq(1L));
        verify(issueRepository).findByOwnerIdAndProjectIdAndId(eq(1L), eq(1L), eq(1L));
    }

    @Test
    public void deleteUserById_shouldReturnDeleteUser_whenFound() {
        when(userRepository.existsById(eq(1L))).thenReturn(true);

        userService.deleteUserById(1L);

        verify(userRepository).existsById(eq(1L));
        verify(userRepository).deleteById(eq(1L));
    }

    @Test
    public void deleteUserById_shouldThrowException_whenNotFound() {
        when(userRepository.existsById(eq(1L))).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(1L));

        verify(userRepository).existsById(eq(1L));
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    public void updateUserById_shouldUpdateAndReturnUser_whenFound() {
        User user = User.builder()
                .id(1L)
                .email("test@email")
                .username("test")
                .password("password")
                .build();

        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User _user = invocation.getArgument(0);
            _user.setId(1L);
            return _user;
        });

        UserRequest request = UserRequest.builder()
                .email("update@email")
                .username("update")
                .password("update_pass")
                .build();

        UserResponse response = userService.updateUserById(1L, request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("update@email", response.getEmail());
        assertEquals("update", response.getUsername());

        verify(userRepository).findById(eq(1L));
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void updateUserById_shouldThrowException_whenNotFound() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.empty());

        UserRequest request = UserRequest.builder()
                .email("update@email")
                .username("update")
                .password("update_pass")
                .build();

        assertThrows(UserNotFoundException.class, () -> userService.updateUserById(1L, request));

        verify(userRepository).findById(eq(1L));
        verify(userRepository, never()).save(any(User.class));
    }

}
