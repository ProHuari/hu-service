package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.CourseRequest;
import com.huariservice.huariia.dto.CourseResponse;
import com.huariservice.huariia.services.CourseService;
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
@RequestMapping("/cursos")
@Tag(name = "Courses", description = "Course catalog management")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @Operation(summary = "Publishes a new course (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Course created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Category or author not found"),
            @ApiResponse(responseCode = "409", description = "Course title or video URL already exists")
    })
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) {
        CourseResponse response = courseService.createCourse(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lists courses with pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated list of courses"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<CourseResponse>> listCourses(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(courseService.listCourses(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Finds a course by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Course found"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<CourseResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Updates an existing course (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Course updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Course, category, or author not found"),
            @ApiResponse(responseCode = "409", description = "Course title or video URL already exists")
    })
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable Long id,
                                                       @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes (soft-delete) a course by ID (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Course deleted"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
