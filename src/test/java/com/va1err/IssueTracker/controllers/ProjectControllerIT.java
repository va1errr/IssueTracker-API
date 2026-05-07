package com.va1err.IssueTracker.controllers;

import com.va1err.IssueTracker.dto.requests.ProjectRequest;
import com.va1err.IssueTracker.models.Project;
import com.va1err.IssueTracker.models.User;
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
public class ProjectControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createProject_shouldCreateProject_whenValidAndFound() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        ProjectRequest request = ProjectRequest.builder()
                .name("test")
                .ownerId(user.getId())
                .build();

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Project created successfully"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.name").value("test"))
                .andExpect(jsonPath("$.data.ownerId").value(user.getId()))
                .andExpect(jsonPath("$.data.issues").isEmpty());

        assertThat(projectRepository.count()).isEqualTo(1);
    }

    @Test
    public void createProject_shouldReturn400_whenNameBlank() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        ProjectRequest request = ProjectRequest.builder()
                .name("")
                .ownerId(user.getId())
                .build();

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").exists());

        assertThat(projectRepository.count()).isEqualTo(0);
    }

    @Test
    public void createProject_shouldReturn404_whenUserNotFound() throws Exception {
        ProjectRequest request = ProjectRequest.builder()
                .name("test")
                .ownerId(1L)
                .build();

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("User with id = 1 not found"));

        assertThat(projectRepository.count()).isEqualTo(0);
    }

    @Test
    public void getAllProjects_shouldReturnAllProjects() throws Exception {
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

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Projects fetched successfully"))
                .andExpect(jsonPath("$.data[0]").exists())
                .andExpect(jsonPath("$.data[0].id").value(project1.getId()))
                .andExpect(jsonPath("$.data[1]").exists())
                .andExpect(jsonPath("$.data[1].id").value(project2.getId()))
                .andExpect(jsonPath("$.data[2]").doesNotExist());
    }

    @Test
    public void getProject_shouldReturnProject_whenFound() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        Project project = projectRepository.save(Project.builder()
                .name("test")
                .owner(user)
                .build());

        mockMvc.perform(get("/projects/" + project.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Project found"))
                .andExpect(jsonPath("$.data.id").value(project.getId()));
    }

    @Test
    public void getProject_shouldReturn404_whenProjectNotFound() throws Exception {
        mockMvc.perform(get("/projects/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("Project with id = 1 not found"));
    }

    @Test
    public void deleteProject_shouldReturnResponse_whenFound() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        Project project = projectRepository.save(Project.builder()
                .name("test")
                .owner(user)
                .build());

        mockMvc.perform(delete("/projects/" + project.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Project found and deleted"))
                .andExpect(jsonPath("$.data.id").value(project.getId()));

        assertThat(projectRepository.count()).isEqualTo(0);
    }

    @Test
    public void deleteProject_shouldReturn404_whenProjectNotFound() throws Exception {
        mockMvc.perform(delete("/projects/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("Project with id = 1 not found"));
    }

    @Test
    public void updateProject_shouldUpdateAndReturnProject_whenValidAndFound() throws Exception {
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

        Project project = projectRepository.save(Project.builder()
                .name("test")
                .owner(user1)
                .build());

        ProjectRequest request = ProjectRequest.builder()
                .name("update test")
                .ownerId(user2.getId())
                .build();

        long projectCountBefore = projectRepository.count();

        mockMvc.perform(put("/projects/" + project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Project found and updated"))
                .andExpect(jsonPath("$.data.id").value(project.getId()))
                .andExpect(jsonPath("$.data.name").value("update test"))
                .andExpect(jsonPath("$.data.ownerId").value(user2.getId()));

        assertThat(projectRepository.count()).isEqualTo(projectCountBefore);
    }

    @Test
    public void updateProject_shouldReturn400_whenNameBlank() throws Exception {
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

        Project project = projectRepository.save(Project.builder()
                .name("test")
                .owner(user1)
                .build());

        ProjectRequest request = ProjectRequest.builder()
                .name("")
                .ownerId(user2.getId())
                .build();

        long projectCountBefore = projectRepository.count();

        mockMvc.perform(put("/projects/" + project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").exists());

        assertThat(projectRepository.count()).isEqualTo(projectCountBefore);
    }

    @Test
    public void updateProject_shouldReturn404_whenProjectNotFound() throws Exception {
        ProjectRequest request = ProjectRequest.builder()
                .name("test")
                .ownerId(1L)
                .build();

        mockMvc.perform(put("/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("Project with id = 1 not found"));

        assertThat(projectRepository.count()).isEqualTo(0);
    }

    @Test
    public void updateProject_shouldReturn404_whenUserNotFound() throws Exception {
        User user = userRepository.save(User.builder()
                .email("test@email")
                .username("test")
                .password("password")
                .build());

        Project project = projectRepository.save(Project.builder()
                .name("test")
                .owner(user)
                .build());

        ProjectRequest request = ProjectRequest.builder()
                .name("test")
                .ownerId(user.getId() + 1)
                .build();

        long projectCountBefore = projectRepository.count();

        mockMvc.perform(put("/projects/" + project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.details.message").value("User with id = " + (user.getId() + 1) + " not found"));

        assertThat(projectRepository.count()).isEqualTo(projectCountBefore);
    }

}
