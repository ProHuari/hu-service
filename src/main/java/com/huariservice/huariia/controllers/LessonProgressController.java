package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.LessonProgressRequest;
import com.huariservice.huariia.dto.LessonProgressResponse;
import com.huariservice.huariia.security.TokenData;
import com.huariservice.huariia.services.LessonProgressService;
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
@RequestMapping("/progresso-aulas")
@Tag(name = "Lesson Progress", description = "Tracking student progress across lessons")
@RequiredArgsConstructor
public class LessonProgressController {

    private final LessonProgressService lessonProgressService;

    @PostMapping
    @Operation(summary = "Records lesson progress for the authenticated user (requires course enrollment)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Progress record created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Lesson not found"),
            @ApiResponse(responseCode = "409", description = "Progress record already exists"),
            @ApiResponse(responseCode = "422", description = "User is not enrolled in the course")
    })
    public ResponseEntity<LessonProgressResponse> createProgress(
            @Valid @RequestBody LessonProgressRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal TokenData currentUser) {
        LessonProgressResponse response =
                lessonProgressService.createProgress(currentUser.userId(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lists lesson progress with pagination (students see their own; admins see all)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated list of lesson progress records"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<LessonProgressResponse>> listProgress(
            @RequestParam(name = "usuarioId", required = false) Long userId,
            @ParameterObject Pageable pageable,
            @Parameter(hidden = true) @AuthenticationPrincipal TokenData currentUser) {
        Page<LessonProgressResponse> progressPage;
        if (!currentUser.isAdmin()) {
            progressPage = lessonProgressService.listProgressByUser(currentUser.userId(), pageable);
        } else if (userId != null) {
            progressPage = lessonProgressService.listProgressByUser(userId, pageable);
        } else {
            progressPage = lessonProgressService.listProgress(pageable);
        }
        return ResponseEntity.ok(progressPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Finds a lesson progress record by ID (students only access their own)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progress record found"),
            @ApiResponse(responseCode = "404", description = "Progress record not found")
    })
    public ResponseEntity<LessonProgressResponse> findById(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal TokenData currentUser) {
        return ResponseEntity.ok(
                lessonProgressService.findById(id, currentUser.userId(), currentUser.isAdmin()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Updates the status of a lesson progress record")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progress record updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Progress record not found")
    })
    public ResponseEntity<LessonProgressResponse> updateProgress(
            @PathVariable Long id,
            @Valid @RequestBody LessonProgressRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal TokenData currentUser) {
        return ResponseEntity.ok(lessonProgressService.updateProgress(
                id, request, currentUser.userId(), currentUser.isAdmin()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes (soft-delete) a lesson progress record by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Progress record deleted"),
            @ApiResponse(responseCode = "404", description = "Progress record not found")
    })
    public ResponseEntity<Void> deleteProgress(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal TokenData currentUser) {
        lessonProgressService.deleteById(id, currentUser.userId(), currentUser.isAdmin());
        return ResponseEntity.noContent().build();
    }
}

