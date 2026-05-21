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

    public Project createProject(UUID personId, Project project) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));

        project.setPerson(person);
        person.getProjects().add(project);
        return projectRepository.save(project);
    }

    public List<Project> getAll() {
        return projectRepository.findAll();
    }

    public void delete(UUID id) {
        projectRepository.deleteById(id);
    }

    public Project getById(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    public Project update(UUID id, Project updated) {
        Project existing = getById(id);
        existing.setProjectName(updated.getProjectName());
        return projectRepository.save(existing);
    }

    @Transactional
    public Project assignProjectToPerson(UUID projectId, UUID personId) throws ValidationException {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        // ✅ FIXED HERE
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));

        if (project.getPerson() != null) {
            throw new ValidationException("Project already has an owner!");
        }

        project.setPerson(person);
        person.getProjects().add(project);

        return projectRepository.save(project);
    }
}