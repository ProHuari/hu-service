package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.LessonProgressRequest;
import com.huariservice.huariia.DTOs.LessonProgressResponse;
import com.huariservice.huariia.security.AuthenticatedUser;
import com.huariservice.huariia.services.LessonProgressService;
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
 * Controller for lesson progress tracking endpoints.
 */
@RestController
@RequestMapping("/lesson-progresses")
@Tag(name = "Lesson Progress")
@RequiredArgsConstructor
public class LessonProgressController {

    private final LessonProgressService lessonProgressService;

    /**
     * Registers lesson progress for the logged-in user.
     *
     * @param request     progress data
     * @param currentUser authenticated user from token
     * @return created progress and location header
     */
    @PostMapping
    @Operation(summary = "Registers lesson progress for the logged-in user")
    public ResponseEntity<LessonProgressResponse> create(
            @Valid @RequestBody LessonProgressRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser currentUser) {
        LessonProgressResponse response = lessonProgressService.create(currentUser.userId(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Lists progress records: students see their own, admins see all (or filtered
     * by userId).
     *
     * @param userId      optional user ID filter for admins
     * @param currentUser authenticated user from token
     * @return list of progress records
     */
    @GetMapping
    @Operation(summary = "Lists progress records (students see own; admins see all or by userId)")
    public ResponseEntity<List<LessonProgressResponse>> findAll(
            @RequestParam(required = false) Long userId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser currentUser) {
        List<LessonProgressResponse> progressList;
        if (!currentUser.isAdmin()) {
            progressList = lessonProgressService.findByUser(currentUser.userId());
        } else if (userId != null) {
            progressList = lessonProgressService.findByUser(userId);
        } else {
            progressList = lessonProgressService.findAll();
        }
        return ResponseEntity.ok(progressList);
    }

    /**
     * Retrieves a progress record by ID (students only access their own).
     *
     * @param id          progress record ID
     * @param currentUser authenticated user from token
     * @return progress record data
     */
    @GetMapping("/{id}")
    @Operation(summary = "Retrieves a progress record by ID (students only access their own)")
    public ResponseEntity<LessonProgressResponse> findById(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser currentUser) {
        return ResponseEntity.ok(
                lessonProgressService.findById(id, currentUser.userId(), currentUser.isAdmin()));
    }

    /**
     * Updates status of a progress record.
     *
     * @param id          progress record ID
     * @param request     updated progress data
     * @param currentUser authenticated user from token
     * @return updated progress record
     */
    @PutMapping("/{id}")
    @Operation(summary = "Updates progress status (user and lesson cannot be changed)")
    public ResponseEntity<LessonProgressResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody LessonProgressRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser currentUser) {
        return ResponseEntity.ok(lessonProgressService.update(
                id, request, currentUser.userId(), currentUser.isAdmin()));
    }

    /**
     * Deletes a progress record (students only delete their own).
     *
     * @param id          progress record ID
     * @param currentUser authenticated user from token
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes a progress record (students only delete their own)")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser currentUser) {
        lessonProgressService.delete(id, currentUser.userId(), currentUser.isAdmin());
        return ResponseEntity.noContent().build();
    }
}
