package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.LessonRequest;
import com.huariservice.huariia.DTOs.LessonResponse;
import com.huariservice.huariia.services.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controller for lesson management endpoints.
 */
@RestController
@RequestMapping("/lessons")
@Tag(name = "Lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    /**
     * Publishes a new lesson (ADMIN only).
     *
     * @param request lesson creation data
     * @return created lesson and location header
     */
    @PostMapping
    @Operation(summary = "Publishes a lesson (ADMIN only)")
    public ResponseEntity<LessonResponse> create(@Valid @RequestBody LessonRequest request) {
        LessonResponse response = lessonService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Lists lessons, optionally filtered by module ID.
     *
     * @param moduleId optional module ID filter
     * @return list of lessons
     */
    @GetMapping
    @Operation(summary = "Lists lessons (use moduleId to filter by module in position order)")
    public ResponseEntity<List<LessonResponse>> findAll(@RequestParam(required = false) Long moduleId) {
        List<LessonResponse> lessons = (moduleId != null)
                ? lessonService.findByModule(moduleId)
                : lessonService.findAll();
        return ResponseEntity.ok(lessons);
    }

    /**
     * Retrieves a lesson by ID.
     *
     * @param id lesson ID
     * @return lesson data
     */
    @GetMapping("/{id}")
    @Operation(summary = "Retrieves a lesson by ID")
    public ResponseEntity<LessonResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.findById(id));
    }

    /**
     * Updates an existing lesson (ADMIN only).
     *
     * @param id      lesson ID
     * @param request updated lesson data
     * @return updated lesson data
     */
    @PutMapping("/{id}")
    @Operation(summary = "Updates a lesson (ADMIN only)")
    public ResponseEntity<LessonResponse> update(@PathVariable Long id,
            @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.ok(lessonService.update(id, request));
    }

    /**
     * Deletes a lesson (ADMIN only).
     *
     * @param id lesson ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes a lesson (ADMIN only)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lessonService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
