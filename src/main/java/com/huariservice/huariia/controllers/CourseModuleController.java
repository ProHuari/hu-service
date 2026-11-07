package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.CourseModuleRequest;
import com.huariservice.huariia.dto.CourseModuleResponse;
import com.huariservice.huariia.services.CourseModuleService;
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
@RequestMapping("/modulos")
@Tag(name = "Course Modules", description = "Ordered sections/modules inside courses")
@RequiredArgsConstructor
public class CourseModuleController {

        private final CourseModuleService courseModuleService;

        @PostMapping
        @Operation(summary = "Creates a new course module (ADMIN only)")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Module created"),
                        @ApiResponse(responseCode = "400", description = "Validation error"),
                        @ApiResponse(responseCode = "404", description = "Course not found"),
                        @ApiResponse(responseCode = "409", description = "Module position already used in the course")
        })
        public ResponseEntity<CourseModuleResponse> createModule(@Valid @RequestBody CourseModuleRequest request) {
                CourseModuleResponse response = courseModuleService.createModule(request);
                URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                .path("/{id}").buildAndExpand(response.id()).toUri();
                return ResponseEntity.created(location).body(response);
        }

        @GetMapping
        @Operation(summary = "Lists course modules with pagination (optional filter by courseId)")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Paginated list of modules")
        })
        public ResponseEntity<Page<CourseModuleResponse>> listModules(
                        @RequestParam(name = "courseId", required = false) Long courseIdParam,
                        @RequestParam(name = "cursoId", required = false) Long cursoIdParam,
                        @ParameterObject Pageable pageable) {
                Long courseId = courseIdParam != null ? courseIdParam : cursoIdParam;
                Page<CourseModuleResponse> page = (courseId != null)
                                ? courseModuleService.listModulesByCourse(courseId, pageable)
                                : courseModuleService.listModules(pageable);
                return ResponseEntity.ok(page);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Finds a course module by ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Module found"),
                        @ApiResponse(responseCode = "404", description = "Module not found")
        })
        public ResponseEntity<CourseModuleResponse> findById(@PathVariable Long id) {
                return ResponseEntity.ok(courseModuleService.findById(id));
        }

        @PutMapping("/{id}")
        @Operation(summary = "Updates a course module (ADMIN only)")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Module updated"),
                        @ApiResponse(responseCode = "400", description = "Validation error"),
                        @ApiResponse(responseCode = "404", description = "Module or course not found"),
                        @ApiResponse(responseCode = "409", description = "Module position already used in the course")
        })
        public ResponseEntity<CourseModuleResponse> updateModule(@PathVariable Long id,
                        @Valid @RequestBody CourseModuleRequest request) {
                return ResponseEntity.ok(courseModuleService.updateModule(id, request));
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Deletes (soft-delete) a course module by ID (ADMIN only)")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Module deleted"),
                        @ApiResponse(responseCode = "404", description = "Module not found")
        })
        public ResponseEntity<Void> deleteModule(@PathVariable Long id) {
                courseModuleService.deleteById(id);
                return ResponseEntity.noContent().build();
        }
}
