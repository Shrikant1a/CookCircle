package com.cookcircle.controller;

import com.cookcircle.dto.ApiResponse;
import com.cookcircle.dto.auth.AuthResponse;
import com.cookcircle.dto.auth.LoginRequest;
import com.cookcircle.dto.auth.RegisterCookRequest;
import com.cookcircle.dto.auth.RegisterResidentRequest;
import com.cookcircle.dto.auth.UserSummaryDto;
import com.cookcircle.security.UserPrincipal;
import com.cookcircle.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication & Onboarding", description = "Endpoints for user login, resident registration, cook onboarding, and profile recovery")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticates user with email and password, returning JWT access token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @PostMapping("/register-resident")
    @Operation(summary = "Register Resident", description = "Onboards an apartment resident into a specific housing society and flat")
    public ResponseEntity<ApiResponse<AuthResponse>> registerResident(@Valid @RequestBody RegisterResidentRequest request) {
        AuthResponse response = authService.registerResident(request);
        return new ResponseEntity<>(ApiResponse.ok("Resident registered successfully", response), HttpStatus.CREATED);
    }

    @PostMapping("/register-cook")
    @Operation(summary = "Register Cook Partner", description = "Registers a new cook partner with background verification queues")
    public ResponseEntity<ApiResponse<AuthResponse>> registerCook(@Valid @RequestBody RegisterCookRequest request) {
        AuthResponse response = authService.registerCook(request);
        return new ResponseEntity<>(ApiResponse.ok("Cook partner registered successfully", response), HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @Operation(summary = "Get Current User Profile", description = "Returns profile and role claims of the currently authenticated JWT token")
    public ResponseEntity<ApiResponse<UserSummaryDto>> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return new ResponseEntity<>(ApiResponse.ok("Unauthenticated", null), HttpStatus.UNAUTHORIZED);
        }
        UserSummaryDto userSummary = authService.getCurrentUser(userPrincipal);
        return ResponseEntity.ok(ApiResponse.ok("Current user retrieved", userSummary));
    }
}

