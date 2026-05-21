package com.andrei.demo.service;

import com.andrei.demo.exception.BadRequestException;
import com.andrei.demo.exception.DuplicateResourceException;
import com.andrei.demo.exception.ResourceNotFoundException;
import com.andrei.demo.model.*;
import com.andrei.demo.repository.PersonRepository;
import com.andrei.demo.repository.ProjectRepository;
import com.andrei.demo.repository.SkillRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.antlr.v4.runtime.misc.LogManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final SkillRepository skillRepository;
    private final ProjectRepository projectRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    public Person create(Person person) {
        if (personRepository.findByEmail(person.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists");
        }
        person.setPassword(
                passwordEncoder.encode(person.getPassword())
        );
        return personRepository.save(person);
    }



    @Transactional
    public void addProjectToPerson(UUID personId, Project project) {

        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person not found"));


        if (project.getPerson() != null) {
            throw new RuntimeException("Project already has an owner!");
        }
        project.setPerson(person);

        person.getProjects().add(project);

        // 💾 save project

        projectRepository.save(project);
    }
    public List<PersonResponseDTO> getPeople() {
        return personRepository.findAll().stream()
                .map(person -> {

                    person.getSkills().size();
                    person.getProjects().size();

                    PersonResponseDTO dto = new PersonResponseDTO();

                    dto.setId(person.getId());
                    dto.setName(person.getName());
                    dto.setAge(person.getAge());
                    dto.setEmail(person.getEmail());

                    dto.setProjects(
                            person.getProjects()
                                    .stream()
                                    .map(p -> p.getProjectName())
                                    .toList()
                    );

                    dto.setSkills(
                            person.getSkills()
                                    .stream()
                                    .map(s -> s.getSkillName())
                                    .toList()
                    );

                    return dto;
                })
                .toList();
    }

    public Person addPerson(PersonCreateDTO personDTO) {
        if (personRepository.findByEmail(personDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists");
        }

        Person person = new Person();

        person.setName(personDTO.getName());
        person.setAge(personDTO.getAge());
        person.setEmail(personDTO.getEmail());
        person.setPassword(personDTO.getPassword());

        auditService.log(
                "CREATE_PERSON",
                person.getEmail()
        );
        return personRepository.save(person);
    }

    public Person updatePerson(UUID uuid, Person person) {
        Optional<Person> personOptional =
                personRepository.findById(uuid);

        if(personOptional.isEmpty()) {
            throw new ResourceNotFoundException("Person with id " + uuid + " not found");
        }
        Person existingPerson = personOptional.get();

        existingPerson.setName(person.getName());
        existingPerson.setAge(person.getAge());
        existingPerson.setEmail(person.getEmail());
        existingPerson.setPassword(person.getPassword());

        auditService.log(
                "UPDATE_PERSON",
                existingPerson.getEmail()
        );
        return personRepository.save(existingPerson);
    }

    public Person updatePerson2(UUID uuid, Person person) {
        return personRepository
                .findById(uuid)
                .map(existingPerson -> {
                    existingPerson.setName(person.getName());
                    existingPerson.setAge(person.getAge());
                    existingPerson.setEmail(person.getEmail());
                    existingPerson.setPassword(person.getPassword());
                    return personRepository.save(existingPerson);
                })
                .orElseThrow(
                        () -> new ResourceNotFoundException("Person with id " + uuid + " not found")
                );
    }

    public void deletePerson(UUID uuid) {
        if (!personRepository.existsById(uuid)) {
            throw new ResourceNotFoundException("Person with id " + uuid + " not found");
        }
        auditService.log(
                "DELETE_PERSON",
                "ADMIN"
        );
        personRepository.deleteById(uuid);
    }

    public Person getPersonByEmail(String email) {
        return personRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Person with email " + email + " not found"));
    }

    public PersonResponseDTO getPersonById(UUID uuid) {
        Person person = personRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));

        // 🔥 FORCE LOAD collections (IMPORTANT)
        person.getSkills().size();
        person.getProjects().size();

        PersonResponseDTO dto = new PersonResponseDTO();

        dto.setId(person.getId());
        dto.setName(person.getName());
        dto.setAge(person.getAge());
        dto.setEmail(person.getEmail());

        dto.setProjects(
                person.getProjects()
                        .stream()
                        .map(p -> p.getProjectName())
                        .toList()
        );

        dto.setSkills(
                person.getSkills()
                        .stream()
                        .map(s -> s.getSkillName())
                        .toList()
        );

        return dto;
    }

    public Person getPersonEntityById(UUID uuid) {
        return personRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));
    }
    public Person patch(UUID id, Map<String, Object> updates) {
        Person person = getPersonEntityById(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> person.setName((String) value);
                case "email" -> person.setEmail((String) value);
                case "age" -> person.setAge((Integer) value);
                case "password" -> person.setPassword((String) value);
                default -> throw new BadRequestException("Invalid field: " + key);
            }
        });

        return personRepository.save(person);
    }


    public Person addSkillToPerson(UUID personId, Integer skillId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));

        if (person.getSkills() == null) {
            person.setSkills(new HashSet<>());
        }

        if (!person.getSkills().contains(skill)) {
            person.getSkills().add(skill);
        }

        return personRepository.save(person);
    }

    @Transactional
    public void deleteSkill(UUID personId, Integer skillId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person not found"));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        person.getSkills().remove(skill);
      //  skill.getPersons().remove(person); // 🔥 important

    }

    public Person assignProject(UUID personId, UUID projectId) {

        Person person = personRepository.findById(personId)
                .orElseThrow();

        Project project = projectRepository.findById(projectId)
                .orElseThrow();

        if (project.getPerson() != null &&
                !project.getPerson().getId().equals(personId)) {

            throw new RuntimeException(
                    "Project already assigned to another person"
            );
        }

        project.setPerson(person);

        projectRepository.save(project);
        auditService.log(
                "ASSIGN_PROJECT",
                person.getEmail()
        );

        return person;
    }

    public Person assignSkill(UUID personId, Integer skillId) {

        Person person = personRepository.findById(personId)
                .orElseThrow();

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow();

        person.getSkills().add(skill);

        return personRepository.save(person);
    }
}