package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.ProgressoAulaRequest;
import com.huariservice.huariia.DTOs.ProgressoAulaResponse;
import com.huariservice.huariia.security.DadosToken;
import com.huariservice.huariia.services.ProgressoAulaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/progresso-aulas")
@Tag(name = "Progresso das aulas")
public class ProgressoAulaController {

    private final ProgressoAulaService progressoAulaService;

    public ProgressoAulaController(ProgressoAulaService progressoAulaService) {
        this.progressoAulaService = progressoAulaService;
    }

    @PostMapping
    @Operation(summary = "Registra o progresso do usuário logado em uma aula (exige matrícula no curso)")
    public ResponseEntity<ProgressoAulaResponse> criar(
            @Valid @RequestBody ProgressoAulaRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        ProgressoAulaResponse response =
                progressoAulaService.pubProgressoAula(usuarioLogado.usuarioId(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Aluno vê o próprio progresso; admin vê todos (filtro opcional usuarioId)")
    public ResponseEntity<List<ProgressoAulaResponse>> listar(
            @RequestParam(required = false) Long usuarioId,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        List<ProgressoAulaResponse> progressos;
        if (!usuarioLogado.isAdmin()) {
            progressos = progressoAulaService.mostrarProgressoDoUsuario(usuarioLogado.usuarioId());
        } else if (usuarioId != null) {
            progressos = progressoAulaService.mostrarProgressoDoUsuario(usuarioId);
        } else {
            progressos = progressoAulaService.mostrarProgressoAula();
        }
        return ResponseEntity.ok(progressos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um progresso pelo id (aluno só acessa o próprio)")
    public ResponseEntity<ProgressoAulaResponse> buscar(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        return ResponseEntity.ok(
                progressoAulaService.buscarPorId(id, usuarioLogado.usuarioId(), usuarioLogado.isAdmin()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza o status do progresso (usuário e aula não mudam)")
    public ResponseEntity<ProgressoAulaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProgressoAulaRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        return ResponseEntity.ok(progressoAulaService.mudarProgressoAula(
                id, request, usuarioLogado.usuarioId(), usuarioLogado.isAdmin()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um progresso (aluno só remove o próprio)")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        progressoAulaService.deletarId(id, usuarioLogado.usuarioId(), usuarioLogado.isAdmin());
        return ResponseEntity.noContent().build();
    }
}