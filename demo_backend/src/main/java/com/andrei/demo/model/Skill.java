package com.andrei.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
//@Data
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "people")
@EqualsAndHashCode(exclude = "people")
@Table(name="skill")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String skillName;

    @ManyToMany(mappedBy = "skills")
    @JsonIgnore
    //@JsonManagedReference
    private List<Person> people = new ArrayList<>();
}
