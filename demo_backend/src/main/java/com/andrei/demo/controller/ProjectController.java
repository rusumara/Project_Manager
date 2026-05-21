package com.andrei.demo.controller;

import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.Project;
import com.andrei.demo.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/{personId}")
    public Project create(
            @PathVariable UUID personId,
            @RequestBody Project project
    ) {
        return projectService.createProject(personId, project);
    }

    @PostMapping("/{projectId}/assign/{personId}")
    public Project assignProjectToPerson(
            @PathVariable UUID projectId,
            @PathVariable UUID personId
    ) throws ValidationException {

        return projectService.assignProjectToPerson(projectId, personId);
    }

    @GetMapping
    public List<Project> getAll() {
        return projectService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        projectService.delete(id);
    }

    @PutMapping("/{id}")
    public Project update(
            @PathVariable UUID id,
            @RequestBody Project project
    ) {
        return projectService.update(id, project);
    }

    @GetMapping("/{id}")
    public Project getById(@PathVariable UUID id) {
        return projectService.getById(id);
    }
}