package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.CourseRequest;
import com.huariservice.huariia.DTOs.CourseResponse;
import com.huariservice.huariia.services.CourseService;
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
 * Controller for course catalog endpoints.
 */
@RestController
@RequestMapping("/courses")
@Tag(name = "Courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * Creates and publishes a new course (ADMIN only).
     *
     * @param request course creation data
     * @return the created course and location header
     */
    @PostMapping
    @Operation(summary = "Publishes a new course (ADMIN only)")
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseRequest request) {
        CourseResponse response = courseService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Lists all published courses.
     *
     * @return list of courses
     */
    @GetMapping
    @Operation(summary = "Lists all published courses")
    public ResponseEntity<List<CourseResponse>> findAll() {
        return ResponseEntity.ok(courseService.findAll());
    }

    /**
     * Retrieves a course by ID.
     *
     * @param id course ID
     * @return course data
     */
    @GetMapping("/{id}")
    @Operation(summary = "Retrieves a course by ID")
    public ResponseEntity<CourseResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.findById(id));
    }

    /**
     * Updates an existing course (ADMIN only).
     *
     * @param id      course ID
     * @param request updated course data
     * @return updated course data
     */
    @PutMapping("/{id}")
    @Operation(summary = "Updates a course (ADMIN only)")
    public ResponseEntity<CourseResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.update(id, request));
    }

    /**
     * Deletes a course (ADMIN only).
     *
     * @param id course ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes a course (ADMIN only)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
