package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.ai.AiTutorChatRequest;
import com.huariservice.huariia.dto.ai.AiTutorChatResponse;
import com.huariservice.huariia.dto.ai.ExplainBillRequest;
import com.huariservice.huariia.dto.ai.ExplainBillResponse;
import com.huariservice.huariia.security.TokenData;
import com.huariservice.huariia.services.GeminiIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ia")
@Tag(name = "Citizen AI Tutor & Plain Language Translator", description = "Generative AI services explaining legislative bills in simple terms and answering citizen questions")
@RequiredArgsConstructor
public class AiCitizenTutorController {

    private final GeminiIntegrationService geminiIntegrationService;

    @PostMapping("/explicar-projeto-lei")
    @Operation(summary = "Translates formal legislative bills ('juridiques') into plain, accessible language with impact analysis")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bill explanation generated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<ExplainBillResponse> explainBill(
            @Valid @RequestBody ExplainBillRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal TokenData currentUser) {
        Long userId = currentUser != null ? currentUser.userId() : null;
        return ResponseEntity.ok(geminiIntegrationService.explainBill(request, userId));
    }

    @PostMapping("/tutor")
    @Operation(summary = "Answers citizen and student questions on civic participation, policy, and public transparency")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tutor answer generated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<AiTutorChatResponse> askTutor(
            @Valid @RequestBody AiTutorChatRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal TokenData currentUser) {
        Long userId = currentUser != null ? currentUser.userId() : null;
        return ResponseEntity.ok(geminiIntegrationService.askTutor(request, userId));
    }
}
