package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.ChangePasswordRequest;
import com.huariservice.huariia.dto.ProfileRequest;
import com.huariservice.huariia.dto.UserRequest;
import com.huariservice.huariia.dto.UserResponse;
import com.huariservice.huariia.security.TokenData;
import com.huariservice.huariia.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Users", description = "User registration, self-service profile, and admin user management")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Returns the profile of the currently authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<UserResponse> getMyProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenData currentUser) {
        return ResponseEntity.ok(userService.findById(currentUser.userId()));
    }

    @PutMapping("/me")
    @Operation(summary = "Updates name and e-mail of the currently authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "E-mail already registered")
    })
    public ResponseEntity<UserResponse> updateMyProfile(
            @Valid @RequestBody ProfileRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal TokenData currentUser) {
        return ResponseEntity.ok(userService.updateProfile(currentUser.userId(), request));
    }

    @PatchMapping("/me/senha")
    @Operation(summary = "Changes the password of the currently authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "422", description = "Current password is incorrect or unchanged")
    })
    public ResponseEntity<Void> changeMyPassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal TokenData currentUser) {
        userService.changePassword(currentUser.userId(), request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @Operation(summary = "Registers a new student user account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Student account created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "E-mail already registered")
    })
    public ResponseEntity<UserResponse> registerStudent(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.registerStudent(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/admin")
    @Operation(summary = "Registers a new administrator account (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Admin account created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "E-mail already registered")
    })
    public ResponseEntity<UserResponse> registerAdmin(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.registerAdmin(request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/usuarios/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lists users with pagination (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated list of users"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Page<UserResponse>> listUsers(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(userService.listUsers(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Finds a user by ID (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Updates a user and resets password (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "E-mail already registered")
    })
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes (soft-delete) a user by ID (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
