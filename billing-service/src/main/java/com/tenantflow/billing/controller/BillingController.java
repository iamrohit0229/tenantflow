package com.tenantflow.billing.controller;

import com.tenantflow.billing.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    @GetMapping("/ping")
    public ResponseEntity<ApiResponse<String>> ping() {
        ApiResponse<String> response = new ApiResponse<>(
                200,
                "Billing Service is alive",
                "pong"
        );
        return ResponseEntity.ok(response);
    }
}
