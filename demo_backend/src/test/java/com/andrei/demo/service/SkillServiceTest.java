package com.andrei.demo.service;

import com.andrei.demo.exception.ResourceNotFoundException;
import com.andrei.demo.model.Person;
import com.andrei.demo.model.Skill;
import com.andrei.demo.repository.PersonRepository;
import com.andrei.demo.repository.SkillRepository;
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
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private SkillService skillService;

    private Skill skill;
    private Person person;

    @BeforeEach
    void setUp() {
        skill = new Skill();
        skill.setId(1);
        skill.setSkillName("Java");
        skill.setPeople(new ArrayList<>());

        person = new Person();
        person.setId(UUID.randomUUID());
        person.setName("Bob");
        person.setSkills(new HashSet<>());
        person.getSkills().add(skill);
        skill.getPeople().add(person);
    }



    @Test
    void create_success() {
        when(skillRepository.save(skill)).thenReturn(skill);

        Skill result = skillService.create(skill);

        assertThat(result).isNotNull();
        assertThat(result.getSkillName()).isEqualTo("Java");
        verify(skillRepository).save(skill);
    }



    @Test
    void getAll_returnsAllSkills() {
        when(skillRepository.findAll()).thenReturn(List.of(skill));

        List<Skill> result = skillService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSkillName()).isEqualTo("Java");
    }

    @Test
    void getAll_empty_returnsEmptyList() {
        when(skillRepository.findAll()).thenReturn(Collections.emptyList());

        List<Skill> result = skillService.getAll();

        assertThat(result).isEmpty();
    }


    @Test
    void getById_success() {
        when(skillRepository.findById(1)).thenReturn(Optional.of(skill));

        Skill result = skillService.getById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getSkillName()).isEqualTo("Java");
    }

    @Test
    void getById_notFound_throwsResourceNotFoundException() {
        when(skillRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> skillService.getById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Skill not found");
    }



    @Test
    void update_success() {
        Skill updated = new Skill();
        updated.setSkillName("Spring Boot");

        when(skillRepository.findById(1)).thenReturn(Optional.of(skill));
        when(skillRepository.save(any(Skill.class))).thenAnswer(inv -> inv.getArgument(0));

        Skill result = skillService.update(1, updated);

        assertThat(result.getSkillName()).isEqualTo("Spring Boot");
    }

    @Test
    void update_notFound_throwsResourceNotFoundException() {
        when(skillRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> skillService.update(99, new Skill()))
                .isInstanceOf(ResourceNotFoundException.class);
    }



    @Test
    void delete_success_removesSkillFromPeopleAndDeletesIt() {
        when(skillRepository.findById(1)).thenReturn(Optional.of(skill));

        skillService.delete(1);

        // Person should no longer have this skill
        assertThat(person.getSkills()).doesNotContain(skill);
        // People list on skill should be cleared
        assertThat(skill.getPeople()).isEmpty();
        verify(skillRepository).delete(skill);
    }

    @Test
    void delete_notFound_throwsResourceNotFoundException() {
        when(skillRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> skillService.delete(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Skill not found");

        verify(skillRepository, never()).delete(any());
    }

    @Test
    void delete_skillWithMultiplePeople_removesFromAll() {
        Person person2 = new Person();
        person2.setId(UUID.randomUUID());
        person2.setSkills(new HashSet<>());
        person2.getSkills().add(skill);
        skill.getPeople().add(person2);

        when(skillRepository.findById(1)).thenReturn(Optional.of(skill));

        skillService.delete(1);

        assertThat(person.getSkills()).doesNotContain(skill);
        assertThat(person2.getSkills()).doesNotContain(skill);
        assertThat(skill.getPeople()).isEmpty();
        verify(skillRepository).delete(skill);
    }
}