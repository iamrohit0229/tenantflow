package com.tenantflow.identity.controller;

import com.tenantflow.identity.dto.ApiResponse;
import com.tenantflow.identity.dto.RegisterRequest;
import com.tenantflow.identity.dto.UserResponse;
import com.tenantflow.identity.service.IdentityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IdentityService identityService;

    public AuthController(IdentityService identityService) {
        this.identityService = identityService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        UserResponse user = identityService.registerUser(request);
        ApiResponse<UserResponse> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "User registered successfully",
                user
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
