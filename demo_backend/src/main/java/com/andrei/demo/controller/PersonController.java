package com.andrei.demo.controller;

import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.PersonCreateDTO;
import com.andrei.demo.model.PersonResponseDTO;
import com.andrei.demo.model.Person;
import com.andrei.demo.service.PersonService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin
public class PersonController {

    private final PersonService personService;

    @GetMapping("/person")
    public List<PersonResponseDTO> getPeople() {
        return personService.getPeople();
    }

    @GetMapping("/person/{uuid}")
    public PersonResponseDTO getPersonById(@PathVariable UUID uuid) {
        return personService.getPersonById(uuid);
    }

    @PostMapping("/person")
    public Person addPerson(
            @Valid @RequestBody PersonCreateDTO personDTO
    ) {
        return personService.addPerson(personDTO);
    }

    @PutMapping("/person/{uuid}")
    public Person updatePerson(
            @PathVariable UUID uuid,
            @RequestBody Person person
    ) throws ValidationException {

        return personService.updatePerson(uuid, person);
    }

    @DeleteMapping("/person/{uuid}")
    public void deletePerson(@PathVariable UUID uuid) {
        personService.deletePerson(uuid);
    }

    @PatchMapping("/person/{uuid}")
    public Person patch(
            @PathVariable UUID uuid,
            @RequestBody Map<String, Object> updates
    ) {
        return personService.patch(uuid, updates);
    }

    @PostMapping("/{uuid}/skills/{skillId}")
    public Person addSkillToPerson(
            @PathVariable UUID uuid,
            @PathVariable Integer skillId
    ) {
        return personService.addSkillToPerson(uuid, skillId);
    }

    @PutMapping("/person/{personId}/skills/{skillId}")
    public Person assignSkill(
            @PathVariable UUID personId,
            @PathVariable Integer skillId
    ) {
        return personService.assignSkill(personId, skillId);
    }

    @PutMapping("/person/{personId}/projects/{projectId}")
    public Person assignProject(
            @PathVariable UUID personId,
            @PathVariable UUID projectId
    ) {
        return personService.assignProject(personId, projectId);
    }
}