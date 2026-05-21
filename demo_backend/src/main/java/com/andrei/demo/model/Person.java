package com.andrei.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"projects", "skills"})
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    private Integer age;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy="person", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    //@JsonIgnore
    private List<Project> projects = new ArrayList<>();

    //private List<Project> projects;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="person_skills",
            joinColumns = @JoinColumn(name="person_id"),
            inverseJoinColumns = @JoinColumn(name="skill_id")
    )
   // @JsonManagedReference
    //@JsonIgnore
    private Set<Skill> skills=new HashSet<>();

    private String resetCode;


    private LocalDateTime resetCodeExpiration;

}
