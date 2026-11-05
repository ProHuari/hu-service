package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.EnrollmentRequest;
import com.huariservice.huariia.dto.EnrollmentResponse;
import com.huariservice.huariia.entities.enums.EnrollmentStatus;
import com.huariservice.huariia.security.TokenData;
import com.huariservice.huariia.services.EnrollmentService;
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
@RequestMapping("/matriculas")
@Tag(name = "Enrollments", description = "Student course enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @Operation(summary = "Enrolls the authenticated user in a course")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Enrollment created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "409", description = "User is already enrolled in the course")
    })
    public ResponseEntity<EnrollmentResponse> enroll(
            @Valid @RequestBody EnrollmentRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal TokenData currentUser) {
        EnrollmentResponse response = enrollmentService.enroll(currentUser.userId(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lists enrollments with pagination (students see their own; admins see all or filter by userId)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated list of enrollments"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<EnrollmentResponse>> listEnrollments(
            @RequestParam(name = "usuarioId", required = false) Long userId,
            @ParameterObject Pageable pageable,
            @Parameter(hidden = true) @AuthenticationPrincipal TokenData currentUser) {
        Page<EnrollmentResponse> enrollments;
        if (!currentUser.isAdmin()) {
            enrollments = enrollmentService.listEnrollmentsByUser(currentUser.userId(), pageable);
        } else if (userId != null) {
            enrollments = enrollmentService.listEnrollmentsByUser(userId, pageable);
        } else {
            enrollments = enrollmentService.listEnrollments(pageable);
        }
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Finds an enrollment by ID (students only access their own)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Enrollment found"),
            @ApiResponse(responseCode = "404", description = "Enrollment not found")
    })
    public ResponseEntity<EnrollmentResponse> findById(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal TokenData currentUser) {
        return ResponseEntity.ok(
                enrollmentService.findById(id, currentUser.userId(), currentUser.isAdmin()));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Updates the status of an enrollment (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Enrollment status updated"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Enrollment not found")
    })
    public ResponseEntity<EnrollmentResponse> updateStatus(@PathVariable Long id,
                                                           @RequestParam EnrollmentStatus status) {
        return ResponseEntity.ok(enrollmentService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes (soft-delete) an enrollment by ID (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Enrollment deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Enrollment not found")
    })
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

