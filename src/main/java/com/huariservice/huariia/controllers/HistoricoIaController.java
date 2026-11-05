package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.AiHistoryRequest;
import com.huariservice.huariia.dto.AiHistoryResponse;
import com.huariservice.huariia.security.DadosToken;
import com.huariservice.huariia.services.AiHistoryService;
import com.huariservice.huariia.services.AiQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

// O histórico é somente leitura: não há PUT.
@RestController
@RequestMapping("/historico-ia")
@Tag(name = "Histórico de IA")
@RequiredArgsConstructor
public class HistoricoIaController {

    private final AiHistoryService aiHistoryService;
    private final AiQueryService aiQueryService;

    @PostMapping
    @Operation(summary = "Envia pergunta à IA e salva no histórico")
    public ResponseEntity<AiHistoryResponse> criar(
            @Valid @RequestBody AiHistoryRequest request,
            @Parameter(hidden = true)
            @AuthenticationPrincipal DadosToken usuarioLogado) {

        AiHistoryResponse response = aiQueryService.query(
                usuarioLogado.usuarioId(),
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
    @Operation(summary = "Envia pergunta à IA com contexto RAG e retorna a resposta via Server-Sent Events (SSE)")
    public SseEmitter criarStream(
            @Valid @RequestBody AiHistoryRequest request,
            @Parameter(hidden = true)
            @AuthenticationPrincipal DadosToken usuarioLogado) {
        return aiQueryService.streamQuery(usuarioLogado.usuarioId(), request.question());
    }

    @GetMapping
    @Operation(summary = "Aluno vê o próprio histórico; admin vê todos (filtro opcional usuarioId)")
    public ResponseEntity<List<AiHistoryResponse>> listar(
            @RequestParam(required = false) Long usuarioId,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        List<AiHistoryResponse> historico;
        if (!usuarioLogado.isAdmin()) {
            historico = aiHistoryService.listHistoryByUser(usuarioLogado.usuarioId());
        } else if (usuarioId != null) {
            historico = aiHistoryService.listHistoryByUser(usuarioId);
        } else {
            historico = aiHistoryService.listAllHistory();
        }
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um registro do histórico pelo id (aluno só acessa o próprio)")
    public ResponseEntity<AiHistoryResponse> buscar(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        return ResponseEntity.ok(
                aiHistoryService.findById(id, usuarioLogado.usuarioId(), usuarioLogado.isAdmin()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um registro do histórico (aluno só remove o próprio)")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        aiHistoryService.deleteById(id, usuarioLogado.usuarioId(), usuarioLogado.isAdmin());
        return ResponseEntity.noContent().build();
    }
}
