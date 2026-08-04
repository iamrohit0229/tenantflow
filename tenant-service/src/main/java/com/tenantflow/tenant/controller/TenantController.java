package com.tenantflow.tenant.controller;

import com.tenantflow.tenant.dto.ApiResponse;
import com.tenantflow.tenant.dto.CreateTenantRequest;
import com.tenantflow.tenant.dto.TenantResponse;
import com.tenantflow.tenant.service.TenantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TenantResponse>> createTenant(
            @Valid @RequestBody CreateTenantRequest request) {
        TenantResponse tenant = tenantService.createTenant(request);
        ApiResponse<TenantResponse> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Tenant created successfully",
                tenant
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
