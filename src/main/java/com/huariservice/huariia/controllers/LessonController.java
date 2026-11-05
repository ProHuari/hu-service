package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.LessonRequest;
import com.huariservice.huariia.dto.LessonResponse;
import com.huariservice.huariia.services.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/aulas")
@Tag(name = "Lessons", description = "Individual video lessons inside course modules")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    @Operation(summary = "Publishes a new lesson (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lesson created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Module not found"),
            @ApiResponse(responseCode = "409", description = "Video URL or module position already used")
    })
    public ResponseEntity<LessonResponse> createLesson(@Valid @RequestBody LessonRequest request) {
        LessonResponse response = lessonService.createLesson(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lists lessons with pagination (optional filter by moduleId)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated list of lessons"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<LessonResponse>> listLessons(
            @RequestParam(name = "moduloId", required = false) Long moduleId,
            @ParameterObject Pageable pageable) {
        Page<LessonResponse> page = (moduleId != null)
                ? lessonService.listLessonsByModule(moduleId, pageable)
                : lessonService.listLessons(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Finds a lesson by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lesson found"),
            @ApiResponse(responseCode = "404", description = "Lesson not found")
    })
    public ResponseEntity<LessonResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Updates a lesson (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lesson updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Lesson or module not found"),
            @ApiResponse(responseCode = "409", description = "Video URL or module position already used")
    })
    public ResponseEntity<LessonResponse> updateLesson(@PathVariable Long id,
                                                       @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.ok(lessonService.updateLesson(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes (soft-delete) a lesson by ID (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Lesson deleted"),
            @ApiResponse(responseCode = "404", description = "Lesson not found")
    })
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

