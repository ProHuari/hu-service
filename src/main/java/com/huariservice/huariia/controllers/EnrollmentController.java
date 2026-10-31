package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.EnrollmentRequest;
import com.huariservice.huariia.DTOs.EnrollmentResponse;
import com.huariservice.huariia.entities.enums.EnrollmentStatus;
import com.huariservice.huariia.security.AuthenticatedUser;
import com.huariservice.huariia.services.EnrollmentService;
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
 * Controller for course enrollments.
 */
@RestController
@RequestMapping("/enrollments")
@Tag(name = "Enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    /**
     * Enrolls the logged-in user in a course.
     *
     * @param request     enrollment request containing courseId
     * @param currentUser authenticated user from token
     * @return created enrollment and location header
     */
    @PostMapping
    @Operation(summary = "Enrolls the logged-in user in a course")
    public ResponseEntity<EnrollmentResponse> create(
            @Valid @RequestBody EnrollmentRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser currentUser) {
        EnrollmentResponse response = enrollmentService.create(currentUser.userId(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Lists enrollments: students see their own, admins see all (or filtered by
     * userId).
     *
     * @param userId      optional filter for admins
     * @param currentUser authenticated user from token
     * @return list of enrollments
     */
    @GetMapping
    @Operation(summary = "Lists enrollments (student sees own; admin sees all or filtered by userId)")
    public ResponseEntity<List<EnrollmentResponse>> findAll(
            @RequestParam(required = false) Long userId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser currentUser) {
        List<EnrollmentResponse> enrollments;
        if (!currentUser.isAdmin()) {
            enrollments = enrollmentService.findByUser(currentUser.userId());
        } else if (userId != null) {
            enrollments = enrollmentService.findByUser(userId);
        } else {
            enrollments = enrollmentService.findAll();
        }
        return ResponseEntity.ok(enrollments);
    }

    /**
     * Retrieves an enrollment by ID (students only access their own).
     *
     * @param id          enrollment ID
     * @param currentUser authenticated user from token
     * @return enrollment data
     */
    @GetMapping("/{id}")
    @Operation(summary = "Retrieves an enrollment by ID (students only access their own)")
    public ResponseEntity<EnrollmentResponse> findById(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser currentUser) {
        return ResponseEntity.ok(
                enrollmentService.findById(id, currentUser.userId(), currentUser.isAdmin()));
    }

    /**
     * Updates enrollment status (ADMIN only).
     *
     * @param id     enrollment ID
     * @param status new enrollment status
     * @return updated enrollment data
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Updates enrollment status (ADMIN only)")
    public ResponseEntity<EnrollmentResponse> updateStatus(@PathVariable Long id,
            @RequestParam EnrollmentStatus status) {
        return ResponseEntity.ok(enrollmentService.updateStatus(id, status));
    }

    /**
     * Deletes an enrollment (ADMIN only).
     *
     * @param id enrollment ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes an enrollment (ADMIN only)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        enrollmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
