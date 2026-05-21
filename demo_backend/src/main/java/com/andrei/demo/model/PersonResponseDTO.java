package com.andrei.demo.model;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PersonResponseDTO {
    private UUID id;
    private String name;
    private Integer age;
    private String email;
    private String role;

    private List<String> projects;
    private List<String> skills;
}