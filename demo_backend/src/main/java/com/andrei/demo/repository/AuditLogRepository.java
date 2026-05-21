package com.andrei.demo.repository;

import com.andrei.demo.model.AuditLog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, UUID> {
}