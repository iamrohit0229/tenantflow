package com.tenantflow.tenant.service;

import com.tenantflow.tenant.dto.CreateTenantRequest;
import com.tenantflow.tenant.dto.TenantResponse;
import com.tenantflow.tenant.entity.Tenant;
import com.tenantflow.tenant.repository.TenantRepository;
import org.springframework.stereotype.Service;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public TenantResponse createTenant(CreateTenantRequest request) {
        Tenant tenant = new Tenant();
        tenant.setName(request.getName());

        Tenant saved = tenantRepository.save(tenant);

        return new TenantResponse(saved.getId(), saved.getName(), saved.getCreatedAt());
    }
}
