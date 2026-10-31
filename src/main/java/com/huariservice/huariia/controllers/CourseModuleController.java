package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.CourseModuleRequest;
import com.huariservice.huariia.DTOs.CourseModuleResponse;
import com.huariservice.huariia.services.CourseModuleService;
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
 * Controller for course module endpoints.
 */
@RestController
@RequestMapping("/modules")
@Tag(name = "Modules")
@RequiredArgsConstructor
public class CourseModuleController {

    private final CourseModuleService courseModuleService;

    /**
     * Creates a new module within a course.
     *
     * @param request module creation data
     * @return created module and location header
     */
    @PostMapping
    @Operation(summary = "Creates a module")
    public ResponseEntity<CourseModuleResponse> create(@Valid @RequestBody CourseModuleRequest request) {
        CourseModuleResponse response = courseModuleService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Lists modules, optionally filtered by course ID and ordered by position.
     *
     * @param courseId optional course ID filter
     * @return list of modules
     */
    @GetMapping
    @Operation(summary = "Lists modules (optionally filtered by courseId)")
    public ResponseEntity<List<CourseModuleResponse>> findAll(@RequestParam(required = false) Long courseId) {
        List<CourseModuleResponse> modules = (courseId != null)
                ? courseModuleService.findByCourse(courseId)
                : courseModuleService.findAll();
        return ResponseEntity.ok(modules);
    }

    /**
     * Retrieves a module by ID.
     *
     * @param id module ID
     * @return module data
     */
    @GetMapping("/{id}")
    @Operation(summary = "Retrieves a module by ID")
    public ResponseEntity<CourseModuleResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(courseModuleService.findById(id));
    }

    /**
     * Updates an existing module.
     *
     * @param id      module ID
     * @param request updated module data
     * @return updated module data
     */
    @PutMapping("/{id}")
    @Operation(summary = "Updates a module")
    public ResponseEntity<CourseModuleResponse> update(@PathVariable Long id,
                                                       @Valid @RequestBody CourseModuleRequest request) {
        return ResponseEntity.ok(courseModuleService.update(id, request));
    }

    /**
     * Deletes a module by ID.
     *
     * @param id module ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes a module")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseModuleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
