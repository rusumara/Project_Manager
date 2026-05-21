package com.andrei.demo.service;

import com.andrei.demo.config.ValidationException;
import com.andrei.demo.exception.ResourceNotFoundException;
import com.andrei.demo.model.Person;
import com.andrei.demo.model.Project;
import com.andrei.demo.repository.PersonRepository;
import com.andrei.demo.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final PersonRepository personRepository;
    private final AuditService auditService;

    public Project createProject(UUID personId, Project project) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));
        project.setPerson(person);
        person.getProjects().add(project);
        Project saved = projectRepository.save(project);
        auditService.log("CREATE_PROJECT", person.getEmail());
        return saved;
    }

    public List<Project> getAll() {
        return projectRepository.findAll();
    }

    public void delete(UUID id) {
        Project project = getById(id);
        auditService.log("DELETE_PROJECT",
                project.getPerson() != null ? project.getPerson().getEmail() : "UNKNOWN");
        projectRepository.deleteById(id);
    }

    public Project getById(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    public Project update(UUID id, Project updated) {
        Project existing = getById(id);
        existing.setProjectName(updated.getProjectName());
        Project saved = projectRepository.save(existing);
        auditService.log("UPDATE_PROJECT",
                saved.getPerson() != null ? saved.getPerson().getEmail() : "UNKNOWN");
        return saved;
    }

    @Transactional
    public Project assignProjectToPerson(UUID projectId, UUID personId) throws ValidationException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));
        if (project.getPerson() != null) {
            throw new ValidationException("Project already has an owner!");
        }
        project.setPerson(person);
        person.getProjects().add(project);
        auditService.log("ASSIGN_PROJECT", person.getEmail());
        return projectRepository.save(project);
    }
}
