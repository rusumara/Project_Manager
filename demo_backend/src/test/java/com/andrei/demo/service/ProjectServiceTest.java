package com.andrei.demo.service;

import com.andrei.demo.config.ValidationException;
import com.andrei.demo.exception.ResourceNotFoundException;
import com.andrei.demo.model.Person;
import com.andrei.demo.model.Project;
import com.andrei.demo.repository.PersonRepository;
import com.andrei.demo.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private ProjectService projectService;

    private Person person;
    private Project project;
    private UUID personId;
    private UUID projectId;

    @BeforeEach
    void setUp() {
        personId = UUID.randomUUID();
        projectId = UUID.randomUUID();

        person = new Person();
        person.setId(personId);
        person.setName("Alice");
        person.setEmail("alice@example.com");
        person.setProjects(new ArrayList<>());

        project = new Project();
        project.setId(projectId);
        project.setProjectName("Alpha Project");
    }

    @Test
    void createProject_success() {
        when(personRepository.findById(personId)).thenReturn(Optional.of(person));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        Project result = projectService.createProject(personId, project);

        assertThat(result.getPerson()).isEqualTo(person);
        assertThat(person.getProjects()).contains(result);
        verify(projectRepository).save(project);
    }

    @Test
    void createProject_personNotFound_throwsResourceNotFoundException() {
        when(personRepository.findById(personId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.createProject(personId, project))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Person not found");

        verify(projectRepository, never()).save(any());
    }

    @Test
    void getAll_returnsAllProjects() {
        when(projectRepository.findAll()).thenReturn(List.of(project));

        List<Project> result = projectService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProjectName()).isEqualTo("Alpha Project");
    }

    @Test
    void getAll_emptyList_returnsEmpty() {
        when(projectRepository.findAll()).thenReturn(Collections.emptyList());

        List<Project> result = projectService.getAll();

        assertThat(result).isEmpty();
    }

    @Test
    void getById_success() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        Project result = projectService.getById(projectId);

        assertThat(result.getId()).isEqualTo(projectId);
        assertThat(result.getProjectName()).isEqualTo("Alpha Project");
    }

    @Test
    void getById_notFound_throwsResourceNotFoundException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getById(projectId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Project not found");
    }

    @Test
    void delete_success() {
        doNothing().when(projectRepository).deleteById(projectId);

        projectService.delete(projectId);

        verify(projectRepository).deleteById(projectId);
    }

    @Test
    void update_success() {
        Project updated = new Project();
        updated.setProjectName("Updated Project");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        Project result = projectService.update(projectId, updated);

        assertThat(result.getProjectName()).isEqualTo("Updated Project");
    }

    @Test
    void update_notFound_throwsResourceNotFoundException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.update(projectId, new Project()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void assignProjectToPerson_success() throws ValidationException {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(personRepository.findById(personId)).thenReturn(Optional.of(person));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        Project result = projectService.assignProjectToPerson(projectId, personId);

        assertThat(result.getPerson()).isEqualTo(person);
        assertThat(person.getProjects()).contains(result);
    }

    @Test
    void assignProjectToPerson_projectAlreadyHasOwner_throwsValidationException() {
        project.setPerson(new Person());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(personRepository.findById(personId)).thenReturn(Optional.of(person));

        assertThatThrownBy(() -> projectService.assignProjectToPerson(projectId, personId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("already has an owner");
    }

    @Test
    void assignProjectToPerson_projectNotFound_throwsResourceNotFoundException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.assignProjectToPerson(projectId, personId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Project not found");
    }

    @Test
    void assignProjectToPerson_personNotFound_throwsResourceNotFoundException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(personRepository.findById(personId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.assignProjectToPerson(projectId, personId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Person not found");
    }
}