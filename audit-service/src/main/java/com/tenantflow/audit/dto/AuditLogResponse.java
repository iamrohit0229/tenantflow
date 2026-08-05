package com.tenantflow.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class AuditLogResponse {

    private String id;
    private String eventType;
    private String message;
    private Instant timestamp;
}
