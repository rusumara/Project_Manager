package com.andrei.demo.service;

import com.andrei.demo.model.AuditLog;

import com.andrei.demo.repository.AuditLogRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository
            auditLogRepository;

    public void log(
            String action,
            String userEmail
    ) {

        AuditLog auditLog =
                new AuditLog();

        auditLog.setAction(action);

        auditLog.setUserEmail(
                userEmail
        );

        auditLog.setTimestamp(
                LocalDateTime.now()
        );

        auditLogRepository.save(
                auditLog
        );
    }
}