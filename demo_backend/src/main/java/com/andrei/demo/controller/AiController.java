package com.andrei.demo.controller;

import com.andrei.demo.model.ChatRequest;
import com.andrei.demo.model.ChatResponse;
import com.andrei.demo.model.Person;
import com.andrei.demo.model.PersonResponseDTO;
import com.andrei.demo.model.PredictionResponse;
import com.andrei.demo.repository.PersonRepository;
import com.andrei.demo.service.AiService;
import com.andrei.demo.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@CrossOrigin
public class AiController {

    private final AiService aiService;
    private final PersonRepository personRepository;
    private final PersonService personService;

    @PostMapping("/predict/{personId}")
    public PredictionResponse predict(@PathVariable UUID personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        List<String> skills = person.getSkills().stream()
                .map(s -> s.getSkillName())
                .toList();

        if (skills.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Person has no skills to predict from");
        }

        return aiService.predict(person.getName(), skills);
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        List<PersonResponseDTO> people = personService.getPeople();
        return aiService.chat(request.getMessage(), List.copyOf(people));
    }
}
