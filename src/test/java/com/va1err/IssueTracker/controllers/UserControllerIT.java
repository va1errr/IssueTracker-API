package com.va1err.IssueTracker.controllers;

import com.va1err.IssueTracker.dto.requests.UserRequest;
import com.va1err.IssueTracker.enums.Role;
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
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Test
    public void createUser_shouldCreateUser_whenValidAndRoleNull() throws Exception {
        UserRequest request = UserRequest.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.email").value("test@email"))
                .andExpect(jsonPath("$.data.username").value("test"))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andExpect(jsonPath("$.data.projects").isEmpty());

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    public void createUser_shouldCreateUser_whenValidAndRoleNotNull() throws Exception {
        UserRequest request = UserRequest.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .role(Role.ADMIN)
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.email").value("test@email"))
                .andExpect(jsonPath("$.data.username").value("test"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"))
                .andExpect(jsonPath("$.data.projects").isEmpty());

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    public void createUser_shouldReturn400_whenEmailBlank() throws Exception {
        UserRequest request = UserRequest.builder()
                .email("")
                .username("test")
                .password("password")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").exists());

        assertThat(userRepository.count()).isEqualTo(0);
    }

    @Test
    public void createUser_shouldReturn400_whenUsernameBlank() throws Exception {
        UserRequest request = UserRequest.builder()
                .email("test@email")
                .username("")
                .password("password")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").exists());

        assertThat(userRepository.count()).isEqualTo(0);
    }

    @Test
    public void createUser_shouldReturn400_whenPasswordLengthLess8() throws Exception {
        UserRequest request = UserRequest.builder()
                .email("test@email")
                .username("test")
                .password("1234567")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").exists());

        assertThat(userRepository.count()).isEqualTo(0);
    }

    @Test
    public void getAllUsers_shouldReturnAllUsers() throws Exception {
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

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Users fetched successfully"))
                .andExpect(jsonPath("$.data[0]").exists())
                .andExpect(jsonPath("$.data[0].id").value(user1.getId()))
                .andExpect(jsonPath("$.data[1]").exists())
                .andExpect(jsonPath("$.data[1].id").value(user2.getId()))
                .andExpect(jsonPath("$.data[2]").doesNotExist());
    }

    @Test
    public void getUser_shouldReturnUser_whenFound() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        mockMvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User found"))
                .andExpect(jsonPath("$.data.id").value(user.getId()));
    }

    @Test
    public void getUser_shouldReturn404_whenUserNotFound() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("User with id = 1 not found"));
    }

    @Test
    public void getUserProjects_shouldReturnProjects_whenFound() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        Project project1 = projectRepository.save(Project.builder()
                .name("test1")
                .owner(user)
                .build());

        Project project2 = projectRepository.save(Project.builder()
                .name("test2")
                .owner(user)
                .build());

        mockMvc.perform(get("/users/" + user.getId() + "/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User projects fetched successfully"))
                .andExpect(jsonPath("$.data[0]").exists())
                .andExpect(jsonPath("$.data[0].id").value(project1.getId()))
                .andExpect(jsonPath("$.data[1]").exists())
                .andExpect(jsonPath("$.data[1].id").value(project2.getId()))
                .andExpect(jsonPath("$.data[2]").doesNotExist());
    }

    @Test
    public void getUserProjects_shouldReturn404_whenUserNotFound() throws Exception {
        mockMvc.perform(get("/users/1/projects"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("User with id = 1 not found"));
    }

    @Test
    public void getUserProject_shouldReturnProject_whenFound() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        Project project = projectRepository.save(Project.builder()
                .name("test")
                .owner(user)
                .build());

        mockMvc.perform(get("/users/" + user.getId() + "/projects/" + project.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User project found"))
                .andExpect(jsonPath("$.data.id").value(project.getId()));
    }

    @Test
    public void getUserProject_shouldReturn404_whenUserNotFound() throws Exception {
        mockMvc.perform(get("/users/1/projects/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("User with id = 1 not found"));
    }

    @Test
    public void getUserProject_shouldReturn404_whenProjectNotFound() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        mockMvc.perform(get("/users/" + user.getId() + "/projects/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("Project with id = 1 not found"));
    }

    @Test
    public void getUserProjectIssues_shouldReturnIssues_whenFound() throws Exception {
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
                .description("")
                .owner(user)
                .project(project)
                .build());

        Issue issue2 = issueRepository.save(Issue.builder()
                .title("test2")
                .description("")
                .owner(user)
                .project(project)
                .build());

        mockMvc.perform(get("/users/" + user.getId() + "/projects/" + project.getId() + "/issues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User project issues fetched successfully"))
                .andExpect(jsonPath("$.data[0]").exists())
                .andExpect(jsonPath("$.data[0].id").value(issue1.getId()))
                .andExpect(jsonPath("$.data[1]").exists())
                .andExpect(jsonPath("$.data[1].id").value(issue2.getId()))
                .andExpect(jsonPath("$.data[2]").doesNotExist());
    }

    @Test
    public void getUserProjectIssues_shouldReturn404_whenUserNotFound() throws Exception {
        mockMvc.perform(get("/users/1/projects/1/issues"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("User with id = 1 not found"));
    }

    @Test
    public void getUserProjectIssues_shouldReturn404_whenProjectNotFound() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        mockMvc.perform(get("/users/" + user.getId() + "/projects/1/issues"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("Project with id = 1 not found"));
    }

    @Test
    public void getUserProjectIssue_shouldReturnIssue_whenFound() throws Exception {
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
                .description("")
                .owner(user)
                .project(project)
                .build());

        mockMvc.perform(get("/users/" + user.getId() + "/projects/" + project.getId() + "/issues/" + issue.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User project issue found"))
                .andExpect(jsonPath("$.data.id").value(issue.getId()));
    }

    @Test
    public void getUserProjectIssue_shouldReturn404_whenUserNotFound() throws Exception {
        mockMvc.perform(get("/users/1/projects/1/issues/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("User with id = 1 not found"));
    }

    @Test
    public void getUserProjectIssue_shouldReturn404_whenProjectNotFound() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        mockMvc.perform(get("/users/" + user.getId() + "/projects/1/issues/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("Project with id = 1 not found"));
    }

    @Test
    public void getUserProjectIssue_shouldReturn404_whenIssueNotFound() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        Project project = projectRepository.save(Project.builder()
                .name("test")
                .owner(user)
                .build());

        mockMvc.perform(get("/users/" + user.getId() + "/projects/" + project.getId() + "/issues/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("Issue with id = 1 not found"));
    }

    @Test
    public void deleteUser_shouldReturnResponse_whenFound() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        mockMvc.perform(delete("/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User found and deleted"))
                .andExpect(jsonPath("$.data.id").value(user.getId()));

        assertThat(userRepository.count()).isEqualTo(0);
    }

    @Test
    public void deleteUser_shouldReturn404_whenUserNotFound() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("User with id = 1 not found"));
    }

    @Test
    public void updateUser_shouldUpdateAndReturnUser_whenValidAndFoundAndRoleNull() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        long userCountBefore = userRepository.count();

        UserRequest request = UserRequest.builder()
                .email("update_test@email")
                .username("update test")
                .password("update password")
                .build();

        mockMvc.perform(put("/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User found and updated"))
                .andExpect(jsonPath("$.data.id").value(user.getId()))
                .andExpect(jsonPath("$.data.email").value("update_test@email"))
                .andExpect(jsonPath("$.data.username").value("update test"))
                .andExpect(jsonPath("$.data.role").value("USER"));

        assertThat(userRepository.count()).isEqualTo(userCountBefore);
    }

    @Test
    public void updateUser_shouldUpdateAndReturnUser_whenValidAndFoundAndRoleNotNull() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        long userCountBefore = userRepository.count();

        UserRequest request = UserRequest.builder()
                .email("update_test@email")
                .username("update test")
                .password("update password")
                .role(Role.ADMIN)
                .build();

        mockMvc.perform(put("/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User found and updated"))
                .andExpect(jsonPath("$.data.id").value(user.getId()))
                .andExpect(jsonPath("$.data.email").value("update_test@email"))
                .andExpect(jsonPath("$.data.username").value("update test"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));

        assertThat(userRepository.count()).isEqualTo(userCountBefore);
    }

    @Test
    public void updateUser_shouldReturn400_whenEmailBlank() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        UserRequest request = UserRequest.builder()
                .email("")
                .username("update test")
                .password("update password")
                .build();

        mockMvc.perform(put("/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").exists());
    }

    @Test
    public void updateUser_shouldReturn400_whenUsernameBlank() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        UserRequest request = UserRequest.builder()
                .email("update_test@email")
                .username("")
                .password("update password")
                .build();

        mockMvc.perform(put("/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").exists());
    }

    @Test
    public void updateUser_shouldReturn400_whenPasswordLengthLess8() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        UserRequest request = UserRequest.builder()
                .email("update_test@email")
                .username("update test")
                .password("1234567")
                .build();

        mockMvc.perform(put("/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").exists());
    }

    @Test
    public void updateUser_shouldReturn404_whenUserNotFound() throws Exception {
        UserRequest request = UserRequest.builder()
                .email("update_test@email")
                .username("update test")
                .password("update password")
                .build();

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("User with id = 1 not found"));
    }

}
