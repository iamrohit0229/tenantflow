package com.tenantflow.audit.controller;

import com.tenantflow.audit.dto.ApiResponse;
import com.tenantflow.audit.dto.AuditLogResponse;
import com.tenantflow.audit.dto.CreateAuditLogRequest;
import com.tenantflow.audit.service.AuditService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @PostMapping("/log")
    public ResponseEntity<ApiResponse<AuditLogResponse>> logEvent(
            @Valid @RequestBody CreateAuditLogRequest request) {
        AuditLogResponse auditLog = auditService.createAuditLog(request);
        ApiResponse<AuditLogResponse> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Audit log created successfully",
                auditLog
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
