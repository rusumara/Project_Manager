package com.andrei.demo.controller;

import com.andrei.demo.model.Skill;
import com.andrei.demo.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public Skill create(@RequestBody Skill skill) {
        return skillService.create(skill);
    }

    @GetMapping
    public List<Skill> getAll() {
        return skillService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        skillService.delete(id);
    }

    @PutMapping("/{id}")
    public Skill update(
            @PathVariable Integer id,
            @RequestBody Skill skill
    ) {
        return skillService.update(id, skill);
    }

    @GetMapping("/{id}")
    public Skill getById(@PathVariable Integer id) {
        return skillService.getById(id);
    }
}