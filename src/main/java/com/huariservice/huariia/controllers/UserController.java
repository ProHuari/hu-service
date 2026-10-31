package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.ChangePasswordRequest;
import com.huariservice.huariia.DTOs.ProfileRequest;
import com.huariservice.huariia.DTOs.UserRequest;
import com.huariservice.huariia.DTOs.UserResponse;
import com.huariservice.huariia.security.AuthenticatedUser;
import com.huariservice.huariia.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controller for user management and profile operations.
 */
@RestController
@RequestMapping("/users")
@Tag(name = "Users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ---------- Current User Profile (id retrieved from token) ----------

    /**
     * Retrieves the profile of the currently authenticated user.
     *
     * @param currentUser authenticated user from token
     * @return current user profile data
     */
    @GetMapping("/me")
    @Operation(summary = "Retrieves profile of the currently logged-in user")
    public ResponseEntity<UserResponse> getCurrentUserProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser currentUser) {
        return ResponseEntity.ok(userService.findById(currentUser.userId()));
    }

    /**
     * Updates name and e-mail of the currently authenticated user.
     *
     * @param request     updated profile details
     * @param currentUser authenticated user from token
     * @return updated user profile data
     */
    @PutMapping("/me")
    @Operation(summary = "Updates name and email of the currently logged-in user")
    public ResponseEntity<UserResponse> updateCurrentUserProfile(
            @Valid @RequestBody ProfileRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser currentUser) {
        return ResponseEntity.ok(userService.updateProfile(currentUser.userId(), request));
    }

    /**
     * Changes the password of the currently authenticated user.
     *
     * @param request     current and new password
     * @param currentUser authenticated user from token
     * @return 204 No Content
     */
    @PatchMapping("/me/password")
    @Operation(summary = "Changes the password of the currently logged-in user")
    public ResponseEntity<Void> changeCurrentUserPassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser currentUser) {
        userService.changePassword(currentUser.userId(), request);
        return ResponseEntity.noContent().build();
    }

    // ---------- Registration ----------

    /**
     * Public user registration with default role STUDENT.
     *
     * @param request user registration data
     * @return the created user and location header
     */
    @PostMapping
    @Operation(summary = "Registers a new user (STUDENT role)")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.createStudent(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Registers a new administrator (ADMIN role required).
     *
     * @param request user registration data
     * @return the created administrator and location header
     */
    @PostMapping("/admin")
    @Operation(summary = "Registers a new administrator (ADMIN only)")
    public ResponseEntity<UserResponse> registerAdmin(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.createAdmin(request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/users/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    // ---------- User Management (ADMIN only) ----------

    /**
     * Lists all registered users.
     *
     * @return list of all users
     */
    @GetMapping
    @Operation(summary = "Lists all users (ADMIN only)")
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id user ID
     * @return user data
     */
    @GetMapping("/{id}")
    @Operation(summary = "Retrieves a user by ID (ADMIN only)")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    /**
     * Updates an existing user and resets password.
     *
     * @param id      user ID
     * @param request updated user data
     * @return updated user data
     */
    @PutMapping("/{id}")
    @Operation(summary = "Updates a user and resets password (ADMIN only)")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    /**
     * Deletes a user by ID.
     *
     * @param id user ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes a user (ADMIN only)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
