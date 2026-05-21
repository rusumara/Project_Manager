package com.andrei.demo.controller;

import com.andrei.demo.model.Skill;
import com.andrei.demo.repository.PersonRepository;
import com.andrei.demo.repository.SkillRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class SkillControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private PersonRepository personRepository;

    private static final String FIXTURE_PATH = "src/test/resources/fixtures/";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        personRepository.deleteAll();
        skillRepository.deleteAll();
        skillRepository.flush();
        seedDatabase();
    }

    private void seedDatabase() throws Exception {
        String seedDataJson = loadFixture("skill_seed.json");
        List<Skill> skills = objectMapper.readValue(seedDataJson, new TypeReference<>() {});
        skillRepository.saveAll(skills);
    }

    @Test
    void testGetSkills() throws Exception {
        mockMvc.perform(get("/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].skillName",
                        Matchers.containsInAnyOrder("Java", "Python")));
    }

    @Test
    void testCreateSkill_ValidPayload() throws Exception {
        String validSkillJson = loadFixture("valid_skill.json");

        mockMvc.perform(post("/skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validSkillJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.skillName").value("Spring Boot"));
    }

    @Test
    void testGetSkillById_Found() throws Exception {
        List<Skill> all = skillRepository.findAll();
        Integer id = all.get(0).getId();

        mockMvc.perform(get("/skills/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void testGetSkillById_NotFound() throws Exception {
        mockMvc.perform(get("/skills/{id}", 9999))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testUpdateSkill() throws Exception {
        List<Skill> all = skillRepository.findAll();
        Integer id = all.get(0).getId();

        String updatedJson = "{\"skillName\": \"Updated Skill\"}";

        mockMvc.perform(put("/skills/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skillName").value("Updated Skill"));
    }

    @Test
    void testDeleteSkill() throws Exception {
        List<Skill> all = skillRepository.findAll();
        Integer id = all.get(0).getId();

        mockMvc.perform(delete("/skills/{id}", id))
                .andExpect(status().isOk());

        mockMvc.perform(get("/skills/{id}", id))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testDeleteSkill_NotFound() throws Exception {
        mockMvc.perform(delete("/skills/{id}", 9999))
                .andExpect(status().is4xxClientError());
    }

    private String loadFixture(String fileName) throws IOException {
        return Files.readString(Paths.get(FIXTURE_PATH + fileName));
    }
}