package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.LoginRequest;
import com.huariservice.huariia.DTOs.LoginResponse;
import com.huariservice.huariia.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for user authentication endpoints.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request credentials (email and password)
     * @return login response containing the token and user details
     */
    @PostMapping("/login")
    @Operation(summary = "Authenticates a user and returns a JWT token")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
