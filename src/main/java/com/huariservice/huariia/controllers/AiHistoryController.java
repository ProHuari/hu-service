package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.AiHistoryRequest;
import com.huariservice.huariia.DTOs.AiHistoryResponse;
import com.huariservice.huariia.security.AuthenticatedUser;
import com.huariservice.huariia.services.AiHistoryService;
import com.huariservice.huariia.services.AiQueryService;
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
 * Controller for AI queries and history consultation.
 */
@RestController
@RequestMapping("/ai-history")
@Tag(name = "AI History")
@RequiredArgsConstructor
public class AiHistoryController {

    private final AiHistoryService aiHistoryService;
    private final AiQueryService aiQueryService;

    /**
     * Sends a question to AI and saves the query to user's history.
     *
     * @param request     question to be asked
     * @param currentUser authenticated user from token
     * @return AI history response with answer
     */
    @PostMapping
    @Operation(summary = "Sends question to AI and records in history")
    public ResponseEntity<AiHistoryResponse> ask(
            @Valid @RequestBody AiHistoryRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser currentUser) {
        AiHistoryResponse response = aiQueryService.ask(
                currentUser.userId(),
                request.question()
        );
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Lists AI consultation history: student views own; admin views all or by userId.
     *
     * @param userId      optional filter by userId for admins
     * @param currentUser authenticated user from token
     * @return list of AI history entries
     */
    @GetMapping
    @Operation(summary = "Lists AI history (students see own; admins see all or by userId)")
    public ResponseEntity<List<AiHistoryResponse>> findAll(
            @RequestParam(required = false) Long userId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser currentUser) {
        List<AiHistoryResponse> history;
        if (!currentUser.isAdmin()) {
            history = aiHistoryService.findByUser(currentUser.userId());
        } else if (userId != null) {
            history = aiHistoryService.findByUser(userId);
        } else {
            history = aiHistoryService.findAll();
        }
        return ResponseEntity.ok(history);
    }

    /**
     * Retrieves an AI consultation record by ID (students only access their own).
     *
     * @param id          history entry ID
     * @param currentUser authenticated user from token
     * @return history entry data
     */
    @GetMapping("/{id}")
    @Operation(summary = "Retrieves an AI history record by ID (students only access their own)")
    public ResponseEntity<AiHistoryResponse> findById(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser currentUser) {
        return ResponseEntity.ok(
                aiHistoryService.findById(id, currentUser.userId(), currentUser.isAdmin()));
    }

    /**
     * Deletes an AI consultation record by ID (students only delete their own).
     *
     * @param id          history entry ID
     * @param currentUser authenticated user from token
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes an AI history record (students only delete their own)")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser currentUser) {
        aiHistoryService.delete(id, currentUser.userId(), currentUser.isAdmin());
        return ResponseEntity.noContent().build();
    }
}
