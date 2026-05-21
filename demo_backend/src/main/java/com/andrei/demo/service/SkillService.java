package com.andrei.demo.service;

import com.andrei.demo.exception.ResourceNotFoundException;
import com.andrei.demo.model.Person;
import com.andrei.demo.model.Skill;
import com.andrei.demo.repository.PersonRepository;
import com.andrei.demo.repository.SkillRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final PersonRepository personRepository;
    private final AuditService auditService;

    public Skill create(Skill skill) {
        Skill saved = skillRepository.save(skill);
        auditService.log("CREATE_SKILL", "ADMIN");
        return saved;
    }

    public List<Skill> getAll() {
        return skillRepository.findAll();
    }

    @Transactional
    public void delete(Integer id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
        for (Person person : skill.getPeople()) {
            person.getSkills().remove(skill);
        }
        skill.getPeople().clear();
        auditService.log("DELETE_SKILL", "ADMIN");
        skillRepository.delete(skill);
    }

    public Skill getById(Integer id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
    }

    public Skill update(Integer id, Skill updated) {
        Skill existing = getById(id);
        existing.setSkillName(updated.getSkillName());
        Skill saved = skillRepository.save(existing);
        auditService.log("UPDATE_SKILL", "ADMIN");
        return saved;
    }
}
