package com.andrei.demo.controller;

import com.andrei.demo.model.Person;
import com.andrei.demo.model.Project;
import com.andrei.demo.repository.PersonRepository;
import com.andrei.demo.repository.ProjectRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class ProjectControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private static final String FIXTURE_PATH = "src/test/resources/fixtures/";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Person savedPerson;

    @BeforeEach
    void setUp() throws Exception {
        projectRepository.deleteAll();
        personRepository.deleteAll();
        personRepository.flush();

        savedPerson = new Person();
        savedPerson.setName("Test Owner");
        savedPerson.setEmail("owner@example.com");
        savedPerson.setAge(30);
        savedPerson.setPassword("StrongPass1!");
        savedPerson = personRepository.save(savedPerson);

        seedDatabase();
    }

    private void seedDatabase() throws Exception {
        String seedDataJson = loadFixture("project_seed.json");
        List<Project> projects = objectMapper.readValue(seedDataJson, new TypeReference<>() {});
        projects.forEach(p -> p.setPerson(savedPerson));
        projectRepository.saveAll(projects);
    }

    @Test
    void testGetProjects() throws Exception {
        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].projectName",
                        Matchers.containsInAnyOrder("Alpha Project", "Beta Project")));
    }

    @Test
    void testCreateProject_Success() throws Exception {
        String body = "{\"projectName\": \"New Project\"}";

        mockMvc.perform(post("/projects/{personId}", savedPerson.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.projectName").value("New Project"));
    }

    @Test
    void testCreateProject_PersonNotFound() throws Exception {
        String body = "{\"projectName\": \"Orphan Project\"}";

        mockMvc.perform(post("/projects/{personId}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetProjectById_Found() throws Exception {
        List<Project> all = projectRepository.findAll();
        UUID id = all.get(0).getId();

        mockMvc.perform(get("/projects/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void testGetProjectById_NotFound() throws Exception {
        mockMvc.perform(get("/projects/{id}", UUID.randomUUID()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testUpdateProject() throws Exception {
        List<Project> all = projectRepository.findAll();
        UUID id = all.get(0).getId();

        String updatedJson = "{\"projectName\": \"Updated Project\"}";

        mockMvc.perform(put("/projects/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectName").value("Updated Project"));
    }

    @Test
    void testDeleteProject() throws Exception {
        List<Project> all = projectRepository.findAll();
        UUID id = all.get(0).getId();

        mockMvc.perform(delete("/projects/{id}", id))
                .andExpect(status().isOk());

        mockMvc.perform(get("/projects/{id}", id))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testAssignProjectToPerson_AlreadyHasOwner() throws Exception {
        List<Project> all = projectRepository.findAll();
        UUID projectId = all.get(0).getId();

        Person anotherPerson = new Person();
        anotherPerson.setName("Another Owner");
        anotherPerson.setEmail("another@example.com");
        anotherPerson.setAge(25);
        anotherPerson.setPassword("StrongPass1!");
        anotherPerson = personRepository.save(anotherPerson);

        mockMvc.perform(post("/projects/{projectId}/assign/{personId}",
                        projectId, anotherPerson.getId()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testAssignProjectToPerson_ProjectNotFound() throws Exception {
        mockMvc.perform(post("/projects/{projectId}/assign/{personId}",
                        UUID.randomUUID(), savedPerson.getId()))
                .andExpect(status().isNotFound());
    }

    private String loadFixture(String fileName) throws IOException {
        return Files.readString(Paths.get(FIXTURE_PATH + fileName));
    }
}