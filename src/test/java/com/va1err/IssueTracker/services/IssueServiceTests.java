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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IssueServiceTests {

    @InjectMocks
    private IssueService issueService;

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    public void createIssue_shouldReturnIssue_whenFound() {
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

        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        when(projectRepository.findByOwnerIdAndId(eq(1L), eq(1L))).thenReturn(Optional.of(project));
        when(issueRepository.save(any(Issue.class))).thenAnswer(injection -> {
            Issue issue = injection.getArgument(0);
            issue.setId(1L);
            return issue;
        });

        IssueRequest request = IssueRequest.builder()
                .title("test")
                .description("test description")
                .status(Status.OPEN)
                .priority(Priority.MEDIUM)
                .projectId(1L)
                .ownerId(1L)
                .build();

        IssueResponse response = issueService.createIssue(request);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("test", response.getTitle());
        assertEquals("test description", response.getDescription());
        assertEquals(Status.OPEN, response.getStatus());
        assertEquals(Priority.MEDIUM, response.getPriority());
        assertEquals(1L, response.getProjectId());
        assertEquals(1L, response.getOwnerId());

        verify(userRepository).findById(eq(1L));
        verify(projectRepository).findByOwnerIdAndId(eq(1L), eq(1L));
        verify(issueRepository).save(any(Issue.class));
    }

    @Test
    public void createIssue_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.empty());

        IssueRequest request = IssueRequest.builder()
                .title("test")
                .description("test description")
                .status(Status.OPEN)
                .priority(Priority.MEDIUM)
                .projectId(1L)
                .ownerId(1L)
                .build();

        assertThrows(UserNotFoundException.class, () -> issueService.createIssue(request));

        verify(userRepository).findById(eq(1L));
        verify(projectRepository, never()).findByOwnerIdAndId(any(), any());
        verify(issueRepository, never()).save(any(Issue.class));
    }

    @Test
    public void createIssue_shouldThrowException_whenProjectNotFound() {
        User user = User.builder()
                .id(1L)
                .email("test@email.com")
                .username("test")
                .password("password")
                .build();

        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        when(projectRepository.findByOwnerIdAndId(eq(1L), eq(1L))).thenReturn(Optional.empty());

        IssueRequest request = IssueRequest.builder()
                .title("test")
                .description("test description")
                .status(Status.OPEN)
                .priority(Priority.MEDIUM)
                .projectId(1L)
                .ownerId(1L)
                .build();

        assertThrows(ProjectNotFoundException.class, () -> issueService.createIssue(request));

        verify(userRepository).findById(eq(1L));
        verify(projectRepository).findByOwnerIdAndId(eq(1L), eq(1L));
        verify(issueRepository, never()).save(any(Issue.class));
    }

    @Test
    public void getAllIssues_shouldReturnAllIssues() {
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

        Issue issue1 = Issue.builder()
                .id(1L)
                .title("test1")
                .description("test description1")
                .status(Status.OPEN)
                .priority(Priority.MEDIUM)
                .owner(user)
                .project(project)
                .build();

        Issue issue2 = Issue.builder()
                .id(2L)
                .title("test2")
                .description("test description2")
                .status(Status.DONE)
                .priority(Priority.HIGH)
                .owner(user)
                .project(project)
                .build();

        List<Issue> issues = Arrays.asList(issue1, issue2);

        when(issueRepository.findAll()).thenReturn(issues);

        List<IssueResponse> response = issueService.getAllIssues();

        assertEquals(2, response.size());
        assertEquals(1L, response.getFirst().getId());
        assertEquals("test1", response.getFirst().getTitle());
        assertEquals("test description1", response.getFirst().getDescription());
        assertEquals(Status.OPEN, response.getFirst().getStatus());
        assertEquals(Priority.MEDIUM, response.getFirst().getPriority());
        assertEquals(1L, response.getFirst().getOwnerId());
        assertEquals(1L, response.getFirst().getProjectId());
        assertEquals(2L, response.get(1).getId());
        assertEquals("test2", response.get(1).getTitle());
        assertEquals("test description2", response.get(1).getDescription());
        assertEquals(Status.DONE, response.get(1).getStatus());
        assertEquals(Priority.HIGH, response.get(1).getPriority());
        assertEquals(1L, response.get(1).getOwnerId());
        assertEquals(1L, response.get(1).getProjectId());

        verify(issueRepository).findAll();
    }

    @Test
    public void getIssueById_shouldReturnIssue_whenFound() {
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

        Issue issue = Issue.builder()
                .id(1L)
                .title("test")
                .description("test description")
                .status(Status.OPEN)
                .priority(Priority.MEDIUM)
                .project(project)
                .owner(user)
                .build();

        when(issueRepository.findById(eq(1L))).thenReturn(Optional.of(issue));

        IssueResponse response = issueService.getIssueById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("test", response.getTitle());
        assertEquals("test description", response.getDescription());
        assertEquals(Status.OPEN, response.getStatus());
        assertEquals(Priority.MEDIUM, response.getPriority());
        assertEquals(1L, response.getOwnerId());
        assertEquals(1L, response.getProjectId());

        verify(issueRepository).findById(eq(1L));
    }

    @Test
    public void getIssueById_shouldThrowException_whenIssueNotFound() {
        when(issueRepository.findById(eq(1L))).thenReturn(Optional.empty());

        assertThrows(IssueNotFoundException.class, () -> issueService.getIssueById(1L));

        verify(issueRepository).findById(eq(1L));
    }

    @Test
    public void deleteIssueById_shouldDeleteIssue_whenFound() {
        when(issueRepository.existsById(eq(1L))).thenReturn(true);

        issueService.deleteIssueById(1L);

        verify(issueRepository).existsById(eq(1L));
        verify(issueRepository).deleteById(eq(1L));
    }

    @Test
    public void deleteIssueById_shouldThrowException_whenIssueNotFound() {
        when(issueRepository.existsById(eq(1L))).thenReturn(false);

        assertThrows(IssueNotFoundException.class, () -> issueService.deleteIssueById(1L));

        verify(issueRepository).existsById(eq(1L));
        verify(issueRepository, never()).deleteById(any());
    }

    @Test
    public void updateIssueById_shouldThrowException_whenFound() {
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

        Project project1 = Project.builder()
                .id(1L)
                .name("test")
                .owner(user1)
                .build();

        Project project2 = Project.builder()
                .id(2L)
                .name("test2")
                .owner(user2)
                .build();

        Issue issue = Issue.builder()
                .id(1L)
                .title("test")
                .description("test description")
                .status(Status.OPEN)
                .priority(Priority.MEDIUM)
                .project(project1)
                .owner(user1)
                .build();

        when(issueRepository.findById(eq(1L))).thenReturn(Optional.of(issue));
        when(userRepository.findById(eq(2L))).thenReturn(Optional.of(user2));
        when(projectRepository.findByOwnerIdAndId(eq(2L), eq(2L))).thenReturn(Optional.of(project2));

        IssueRequest request = IssueRequest.builder()
                .title("update test")
                .description("update test description")
                .status(Status.DONE)
                .priority(Priority.LOW)
                .ownerId(2L)
                .projectId(2L)
                .build();

        IssueResponse response = issueService.updateIssueById(1L, request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("update test", response.getTitle());
        assertEquals("update test description", response.getDescription());
        assertEquals(Status.DONE, response.getStatus());
        assertEquals(Priority.LOW, response.getPriority());
        assertEquals(2L, response.getOwnerId());
        assertEquals(2L, response.getProjectId());

        verify(issueRepository).findById(eq(1L));
        verify(userRepository).findById(eq(2L));
        verify(projectRepository).findByOwnerIdAndId(eq(2L), eq(2L));
    }

    @Test
    public void updateIssueById_shouldThrowException_whenIssueNotFound() {
        when(issueRepository.findById(eq(1L))).thenReturn(Optional.empty());

        IssueRequest request = IssueRequest.builder()
                .title("update test")
                .description("update test description")
                .status(Status.DONE)
                .priority(Priority.LOW)
                .ownerId(2L)
                .projectId(2L)
                .build();

        assertThrows(IssueNotFoundException.class, () -> issueService.updateIssueById(1L, request));

        verify(issueRepository).findById(eq(1L));
        verify(userRepository, never()).findById(any());
        verify(projectRepository, never()).findByOwnerIdAndId(any(), any());
    }

    @Test
    public void updateIssueById_shouldThrowException_whenUserNotFound() {
        User user1 = User.builder()
                .id(1L)
                .email("test1@email.com")
                .username("test1")
                .password("password1")
                .build();

        Project project1 = Project.builder()
                .id(1L)
                .name("test")
                .owner(user1)
                .build();

        Issue issue = Issue.builder()
                .id(1L)
                .title("test")
                .description("test description")
                .status(Status.OPEN)
                .priority(Priority.MEDIUM)
                .project(project1)
                .owner(user1)
                .build();

        IssueRequest request = IssueRequest.builder()
                .title("update test")
                .description("update test description")
                .status(Status.DONE)
                .priority(Priority.LOW)
                .ownerId(2L)
                .projectId(2L)
                .build();

        when(issueRepository.findById(eq(1L))).thenReturn(Optional.of(issue));
        when(userRepository.findById(eq(2L))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> issueService.updateIssueById(1L, request));

        verify(issueRepository).findById(eq(1L));
        verify(userRepository).findById(eq(2L));
        verify(projectRepository, never()).findByOwnerIdAndId(any(), any());
    }

    @Test
    public void updateIssueById_shouldThrowException_whenProjectNotFound() {
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

        Project project1 = Project.builder()
                .id(1L)
                .name("test")
                .owner(user1)
                .build();

        Issue issue = Issue.builder()
                .id(1L)
                .title("test")
                .description("test description")
                .status(Status.OPEN)
                .priority(Priority.MEDIUM)
                .project(project1)
                .owner(user1)
                .build();

        when(issueRepository.findById(eq(1L))).thenReturn(Optional.of(issue));
        when(userRepository.findById(eq(2L))).thenReturn(Optional.of(user2));
        when(projectRepository.findByOwnerIdAndId(eq(2L), eq(2L))).thenReturn(Optional.empty());

        IssueRequest request = IssueRequest.builder()
                .title("update test")
                .description("update test description")
                .status(Status.DONE)
                .priority(Priority.LOW)
                .ownerId(2L)
                .projectId(2L)
                .build();

        assertThrows(ProjectNotFoundException.class, () -> issueService.updateIssueById(1L, request));

        verify(issueRepository).findById(eq(1L));
        verify(userRepository).findById(eq(2L));
        verify(projectRepository).findByOwnerIdAndId(eq(2L), eq(2L));
    }

}
