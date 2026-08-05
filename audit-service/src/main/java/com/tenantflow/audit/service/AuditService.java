package com.tenantflow.audit.service;

import com.tenantflow.audit.dto.AuditLogResponse;
import com.tenantflow.audit.dto.CreateAuditLogRequest;
import com.tenantflow.audit.entity.AuditLog;
import com.tenantflow.audit.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public AuditLogResponse createAuditLog(CreateAuditLogRequest request) {
        AuditLog auditLog = new AuditLog();
        auditLog.setEventType(request.getEventType());
        auditLog.setMessage(request.getMessage());
        auditLog.setTimestamp(Instant.now());

        AuditLog saved = auditLogRepository.save(auditLog);

        return new AuditLogResponse(
                saved.getId(),
                saved.getEventType(),
                saved.getMessage(),
                saved.getTimestamp()
        );
    }
}
