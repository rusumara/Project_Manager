package com.andrei.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
@Getter
@Setter
@Table(name="project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String projectName;

    @ManyToOne
    @JoinColumn(name="person_id", nullable=false)
   @JsonBackReference
   // @JsonIgnore
    private Person person;
}
