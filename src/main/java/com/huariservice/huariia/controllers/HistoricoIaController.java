package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.HistoricoIaRequest;
import com.huariservice.huariia.DTOs.HistoricoIaResponse;
import com.huariservice.huariia.security.DadosToken;
import com.huariservice.huariia.services.HistoricoIaService;
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

// O histórico é somente leitura: não há PUT.
@RestController
@RequestMapping("/historico-ia")
@Tag(name = "Histórico de IA")
public class HistoricoIaController {

    private final HistoricoIaService historicoIaService;

    public HistoricoIaController(HistoricoIaService historicoIaService) {
        this.historicoIaService = historicoIaService;
    }


    @PostMapping
    @Operation(summary = "Registra uma consulta à IA no histórico do usuário logado")
    public ResponseEntity<Void> criar(
            @Valid @RequestBody HistoricoIaRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {

        return ResponseEntity.status(501).build();
    }


    @GetMapping
    @Operation(summary = "Aluno vê o próprio histórico; admin vê todos (filtro opcional usuarioId)")
    public ResponseEntity<List<HistoricoIaResponse>> listar(
            @RequestParam(required = false) Long usuarioId,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        List<HistoricoIaResponse> historico;
        if (!usuarioLogado.isAdmin()) {
            historico = historicoIaService.mostrarHistoricoDoUsuario(usuarioLogado.usuarioId());
        } else if (usuarioId != null) {
            historico = historicoIaService.mostrarHistoricoDoUsuario(usuarioId);
        } else {
            historico = historicoIaService.mostrarHistoricoIa();
        }
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um registro do histórico pelo id (aluno só acessa o próprio)")
    public ResponseEntity<HistoricoIaResponse> buscar(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        return ResponseEntity.ok(
                historicoIaService.buscarPorId(id, usuarioLogado.usuarioId(), usuarioLogado.isAdmin()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um registro do histórico (aluno só remove o próprio)")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        historicoIaService.deletarId(id, usuarioLogado.usuarioId(), usuarioLogado.isAdmin());
        return ResponseEntity.noContent().build();
    }
}