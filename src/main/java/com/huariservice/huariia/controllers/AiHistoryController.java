package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.AiHistoryRequest;
import com.huariservice.huariia.dto.AiHistoryResponse;
import com.huariservice.huariia.security.TokenData;
import com.huariservice.huariia.services.AiHistoryService;
import com.huariservice.huariia.services.AiQueryService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/historico-ia")
@Tag(name = "AI Assistant & History", description = "Civic/political AI queries (with RAG & SSE streaming) and history")
@RequiredArgsConstructor
public class AiHistoryController {

    private final AiHistoryService aiHistoryService;
    private final AiQueryService aiQueryService;

    @PostMapping
    @Operation(summary = "Sends a question to the AI assistant (with RAG context) and saves the response in history")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "AI answer generated and saved"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "502", description = "AI provider failed to return a valid answer"),
            @ApiResponse(responseCode = "503", description = "AI service disabled or rate-limited")
    })
    public ResponseEntity<AiHistoryResponse> queryAi(
            @Valid @RequestBody AiHistoryRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal TokenData currentUser) {

        AiHistoryResponse response = aiQueryService.query(
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

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Sends a question to the AI assistant with RAG context and streams the response via SSE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SSE stream opened"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public SseEmitter streamAiQuery(
            @Valid @RequestBody AiHistoryRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal TokenData currentUser) {
        return aiQueryService.streamQuery(currentUser.userId(), request.question());
    }

    @GetMapping
    @Operation(summary = "Lists AI query history with pagination (students see their own; admins see all)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated list of AI history records"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<AiHistoryResponse>> listHistory(
            @RequestParam(name = "usuarioId", required = false) Long userId,
            @ParameterObject Pageable pageable,
            @Parameter(hidden = true) @AuthenticationPrincipal TokenData currentUser) {
        Page<AiHistoryResponse> historyPage;
        if (!currentUser.isAdmin()) {
            historyPage = aiHistoryService.listHistoryByUser(currentUser.userId(), pageable);
        } else if (userId != null) {
            historyPage = aiHistoryService.listHistoryByUser(userId, pageable);
        } else {
            historyPage = aiHistoryService.listAllHistory(pageable);
        }
        return ResponseEntity.ok(historyPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Finds an AI history entry by ID (students only access their own)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "History entry found"),
            @ApiResponse(responseCode = "404", description = "History entry not found")
    })
    public ResponseEntity<AiHistoryResponse> findById(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal TokenData currentUser) {
        return ResponseEntity.ok(
                aiHistoryService.findById(id, currentUser.userId(), currentUser.isAdmin()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes (soft-delete) an AI history entry by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "History entry deleted"),
            @ApiResponse(responseCode = "404", description = "History entry not found")
    })
    public ResponseEntity<Void> deleteById(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal TokenData currentUser) {
        aiHistoryService.deleteById(id, currentUser.userId(), currentUser.isAdmin());
        return ResponseEntity.noContent().build();
    }
}

