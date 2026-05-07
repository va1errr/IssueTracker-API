package com.va1err.IssueTracker.controllers;

import com.va1err.IssueTracker.dto.requests.IssueRequest;
import com.va1err.IssueTracker.enums.Priority;
import com.va1err.IssueTracker.enums.Status;
import com.va1err.IssueTracker.models.Issue;
import com.va1err.IssueTracker.models.Project;
import com.va1err.IssueTracker.models.User;
import com.va1err.IssueTracker.repositories.IssueRepository;
import com.va1err.IssueTracker.repositories.ProjectRepository;
import com.va1err.IssueTracker.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class IssueControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    public void createIssue_shouldCreateIssue_whenValidAndFoundAndDescriptionStatusPriorityNull() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        Project project = projectRepository.save(Project.builder()
                .name("test")
                .owner(user)
                .build());

        IssueRequest request = IssueRequest.builder()
                .title("test")
                .ownerId(user.getId())
                .projectId(project.getId())
                .build();

        mockMvc.perform(post("/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Issue created successfully"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.title").value("test"))
                .andExpect(jsonPath("$.data.description").value(""))
                .andExpect(jsonPath("$.data.status").value("OPEN"))
                .andExpect(jsonPath("$.data.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.data.ownerId").value(user.getId()))
                .andExpect(jsonPath("$.data.projectId").value(project.getId()));

        assertThat(issueRepository.count()).isEqualTo(1);
    }

    @Test
    public void createIssue_shouldCreateIssue_whenValidAndFoundAndDescriptionStatusPriorityNotNull() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        Project project = projectRepository.save(Project.builder()
                .name("test")
                .owner(user)
                .build());

        IssueRequest request = IssueRequest.builder()
                .title("test")
                .description("test description")
                .status(Status.DONE)
                .priority(Priority.HIGH)
                .ownerId(user.getId())
                .projectId(project.getId())
                .build();

        mockMvc.perform(post("/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Issue created successfully"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.title").value("test"))
                .andExpect(jsonPath("$.data.description").value("test description"))
                .andExpect(jsonPath("$.data.status").value("DONE"))
                .andExpect(jsonPath("$.data.priority").value("HIGH"))
                .andExpect(jsonPath("$.data.ownerId").value(user.getId()))
                .andExpect(jsonPath("$.data.projectId").value(project.getId()));

        assertThat(issueRepository.count()).isEqualTo(1);
    }

    @Test
    public void createIssue_shouldReturn400_whenTitleBlank() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        Project project = projectRepository.save(Project.builder()
                .name("test")
                .owner(user)
                .build());

        IssueRequest request = IssueRequest.builder()
                .title("")
                .ownerId(user.getId())
                .projectId(project.getId())
                .build();

        mockMvc.perform(post("/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").exists());
    }

    @Test
    public void createIssue_shouldReturn404_whenUserNotFound() throws Exception {
        IssueRequest request = IssueRequest.builder()
                .title("test")
                .ownerId(1L)
                .projectId(1L)
                .build();

        mockMvc.perform(post("/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("User with id = 1 not found"));
    }

    @Test
    public void createIssue_shouldReturn404_whenProjectNotFound() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        IssueRequest request = IssueRequest.builder()
                .title("test")
                .ownerId(user.getId())
                .projectId(1L)
                .build();

        mockMvc.perform(post("/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("Project with id = 1 not found"));
    }

    @Test
    public void getAllIssues_shouldReturnAllIssues() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        Project project = projectRepository.save(Project.builder()
                .name("test")
                .owner(user)
                .build());

        Issue issue1 = issueRepository.save(Issue.builder()
                .title("test1")
                .owner(user)
                .project(project)
                .build());

        Issue issue2 = issueRepository.save(Issue.builder()
                .title("test2")
                .owner(user)
                .project(project)
                .build());

        mockMvc.perform(get("/issues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Issues fetched successfully"))
                .andExpect(jsonPath("$.data[0]").exists())
                .andExpect(jsonPath("$.data[0].id").value(issue1.getId()))
                .andExpect(jsonPath("$.data[1]").exists())
                .andExpect(jsonPath("$.data[1].id").value(issue2.getId()))
                .andExpect(jsonPath("$.data[2]").doesNotExist());
    }

    @Test
    public void getIssue_shouldReturnIssue_whenFound() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        Project project = projectRepository.save(Project.builder()
                .name("test")
                .owner(user)
                .build());

        Issue issue = issueRepository.save(Issue.builder()
                .title("test")
                .owner(user)
                .project(project)
                .build());

        mockMvc.perform(get("/issues/" + issue.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Issue found"))
                .andExpect(jsonPath("$.data.id").value(issue.getId()));
    }

    @Test
    public void getIssue_shouldReturn404_whenIssueNotFound() throws Exception {
        mockMvc.perform(get("/issues/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("Issue with id = 1 not found"));
    }

    @Test
    public void deleteIssue_shouldReturnResponse_whenFound() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        Project project = projectRepository.save(Project.builder()
                .name("test")
                .owner(user)
                .build());

        Issue issue = issueRepository.save(Issue.builder()
                .title("test")
                .owner(user)
                .project(project)
                .build());

        mockMvc.perform(delete("/issues/" + issue.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Issue found and deleted"))
                .andExpect(jsonPath("$.data.id").value(issue.getId()));

        assertThat(issueRepository.count()).isEqualTo(0);
    }

    @Test
    public void deleteIssue_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(delete("/issues/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("Issue with id = 1 not found"));
    }

    @Test
    public void updateIssue_shouldUpdateAndReturnIssue_whenValidAndFoundAndDescriptionStatusPriorityNull() throws Exception {
        User user1 = userRepository.save(User.builder()
                .email("test1@email")
                .username("test1")
                .password("password1")
                .build());

        User user2 = userRepository.save(User.builder()
                .email("test2@email")
                .username("test2")
                .password("password2")
                .build());

        Project project1 = projectRepository.save(Project.builder()
                .name("test1")
                .owner(user1)
                .build());

        Project project2 = projectRepository.save(Project.builder()
                .name("test2")
                .owner(user2)
                .build());

        Issue issue = issueRepository.save(Issue.builder()
                .title("test")
                .owner(user1)
                .project(project1)
                .build());

        IssueRequest request = IssueRequest.builder()
                .title("update test")
                .ownerId(user2.getId())
                .projectId(project2.getId())
                .build();

        long issueCountBefore = issueRepository.count();

        mockMvc.perform(put("/issues/" + issue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Issue found and updated"))
                .andExpect(jsonPath("$.data.id").value(issue.getId()))
                .andExpect(jsonPath("$.data.title").value("update test"))
                .andExpect(jsonPath("$.data.description").value(""))
                .andExpect(jsonPath("$.data.status").value("OPEN"))
                .andExpect(jsonPath("$.data.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.data.ownerId").value(user2.getId()))
                .andExpect(jsonPath("$.data.projectId").value(project2.getId()));

        assertThat(issueRepository.count()).isEqualTo(issueCountBefore);
    }

    @Test
    public void updateIssue_shouldUpdateAndReturnIssue_whenValidAndFoundAndDescriptionStatusPriorityNotNull() throws Exception {
        User user1 = userRepository.save(User.builder()
                .email("test1@email")
                .username("test1")
                .password("password1")
                .build());

        User user2 = userRepository.save(User.builder()
                .email("test2@email")
                .username("test2")
                .password("password2")
                .build());

        Project project1 = projectRepository.save(Project.builder()
                .name("test1")
                .owner(user1)
                .build());

        Project project2 = projectRepository.save(Project.builder()
                .name("test2")
                .owner(user2)
                .build());

        Issue issue = issueRepository.save(Issue.builder()
                .title("test")
                .owner(user1)
                .project(project1)
                .build());

        IssueRequest request = IssueRequest.builder()
                .title("update test")
                .description("update description")
                .status(Status.DONE)
                .priority(Priority.HIGH)
                .ownerId(user2.getId())
                .projectId(project2.getId())
                .build();

        long issueCountBefore = issueRepository.count();

        mockMvc.perform(put("/issues/" + issue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Issue found and updated"))
                .andExpect(jsonPath("$.data.id").value(issue.getId()))
                .andExpect(jsonPath("$.data.title").value("update test"))
                .andExpect(jsonPath("$.data.description").value("update description"))
                .andExpect(jsonPath("$.data.status").value("DONE"))
                .andExpect(jsonPath("$.data.priority").value("HIGH"))
                .andExpect(jsonPath("$.data.ownerId").value(user2.getId()))
                .andExpect(jsonPath("$.data.projectId").value(project2.getId()));

        assertThat(issueRepository.count()).isEqualTo(issueCountBefore);
    }

    @Test
    public void updateIssue_shouldReturn400_whenTitleBlank() throws Exception {
        User user1 = userRepository.save(User.builder()
                .email("test1@email")
                .username("test1")
                .password("password1")
                .build());

        User user2 = userRepository.save(User.builder()
                .email("test2@email")
                .username("test2")
                .password("password2")
                .build());

        Project project1 = projectRepository.save(Project.builder()
                .name("test1")
                .owner(user1)
                .build());

        Project project2 = projectRepository.save(Project.builder()
                .name("test2")
                .owner(user2)
                .build());

        Issue issue = issueRepository.save(Issue.builder()
                .title("test")
                .owner(user1)
                .project(project1)
                .build());

        IssueRequest request = IssueRequest.builder()
                .title("")
                .ownerId(user2.getId())
                .projectId(project2.getId())
                .build();

        long issueCountBefore = issueRepository.count();

        mockMvc.perform(put("/issues/" + issue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").exists());

        assertThat(issueRepository.count()).isEqualTo(issueCountBefore);
    }

    @Test
    public void updateIssue_shouldReturn404_whenIssueNotFound() throws Exception {
        IssueRequest request = IssueRequest.builder()
                .title("update test")
                .ownerId(1L)
                .projectId(1L)
                .build();

        mockMvc.perform(put("/issues/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("Issue with id = 1 not found"));
    }

    @Test
    public void updateIssue_shouldReturn404_whenUserNotFound() throws Exception {
        User user1 = userRepository.save(User.builder()
                .email("test1@email")
                .username("test1")
                .password("password1")
                .build());

        Project project1 = projectRepository.save(Project.builder()
                .name("test1")
                .owner(user1)
                .build());

        Issue issue = issueRepository.save(Issue.builder()
                .title("test")
                .owner(user1)
                .project(project1)
                .build());

        IssueRequest request = IssueRequest.builder()
                .title("update test")
                .ownerId(user1.getId() + 1)
                .projectId(1L)
                .build();

        mockMvc.perform(put("/issues/" + issue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("User with id = " + (user1.getId() + 1) + " not found"));
    }

    @Test
    public void updateIssue_shouldReturn404_whenProjectNotFound() throws Exception {
        User user1 = userRepository.save(User.builder()
                .email("test1@email")
                .username("test1")
                .password("password1")
                .build());

        User user2 = userRepository.save(User.builder()
                .email("test2@email")
                .username("test2")
                .password("password2")
                .build());

        Project project1 = projectRepository.save(Project.builder()
                .name("test1")
                .owner(user1)
                .build());

        Issue issue = issueRepository.save(Issue.builder()
                .title("test")
                .owner(user1)
                .project(project1)
                .build());

        IssueRequest request = IssueRequest.builder()
                .title("update test")
                .ownerId(user2.getId())
                .projectId(1L)
                .build();

        mockMvc.perform(put("/issues/" + issue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("Project with id = 1 not found"));
    }
}
