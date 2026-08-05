package com.tenantflow.audit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAuditLogRequest {

    @NotBlank
    private String eventType;

    @NotBlank
    private String message;
}
