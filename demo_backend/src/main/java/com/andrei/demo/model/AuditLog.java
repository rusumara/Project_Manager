package com.andrei.demo.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class AuditLog {

    @Id
    @GeneratedValue
    private UUID id;

    private String action;

    private String userEmail;

    private LocalDateTime timestamp;
}