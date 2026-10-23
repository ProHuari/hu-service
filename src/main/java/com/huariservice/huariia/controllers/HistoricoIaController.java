package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.HistoricoIaRequest;
import com.huariservice.huariia.DTOs.HistoricoIaResponse;
import com.huariservice.huariia.security.DadosToken;
import com.huariservice.huariia.services.ConsultaIaService;
import com.huariservice.huariia.services.HistoricoIaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

// O histórico é somente leitura: não há PUT.
@RestController
@RequestMapping("/historico-ia")
@Tag(name = "Histórico de IA")
@RequiredArgsConstructor
public class HistoricoIaController {

    private final HistoricoIaService historicoIaService;
    private final ConsultaIaService consultaIaService;




    @PostMapping
    @Operation(summary = "Envia pergunta à IA e salva no histórico")
    public ResponseEntity<HistoricoIaResponse> criar(
            @Valid @RequestBody HistoricoIaRequest request,
            @Parameter(hidden = true)
            @AuthenticationPrincipal DadosToken usuarioLogado) {

        HistoricoIaResponse response = consultaIaService.consultar(
                usuarioLogado.usuarioId(),
                request.pergunta()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
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