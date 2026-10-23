package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.MatriculaRequest;
import com.huariservice.huariia.DTOs.MatriculaResponse;
import com.huariservice.huariia.entities.enums.StatusMatricula;
import com.huariservice.huariia.security.DadosToken;
import com.huariservice.huariia.services.MatriculaService;
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

@RestController
@RequestMapping("/matriculas")
@Tag(name = "Matrículas")
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaService matriculaService;


    @PostMapping
    @Operation(summary = "Matricula o usuário logado em um curso")
    public ResponseEntity<MatriculaResponse> criar(
            @Valid @RequestBody MatriculaRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        MatriculaResponse response = matriculaService.pubMatricula(usuarioLogado.usuarioId(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Aluno vê as próprias matrículas; admin vê todas (filtro opcional usuarioId)")
    public ResponseEntity<List<MatriculaResponse>> listar(
            @RequestParam(required = false) Long usuarioId,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        List<MatriculaResponse> matriculas;
        if (!usuarioLogado.isAdmin()) {
            matriculas = matriculaService.mostrarMatriculasDoUsuario(usuarioLogado.usuarioId());
        } else if (usuarioId != null) {
            matriculas = matriculaService.mostrarMatriculasDoUsuario(usuarioId);
        } else {
            matriculas = matriculaService.mostrarMatriculas();
        }
        return ResponseEntity.ok(matriculas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma matrícula pelo id (aluno só acessa as próprias)")
    public ResponseEntity<MatriculaResponse> buscar(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        return ResponseEntity.ok(
                matriculaService.buscarPorId(id, usuarioLogado.usuarioId(), usuarioLogado.isAdmin()));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Altera o status da matrícula (somente ADMIN)")
    public ResponseEntity<MatriculaResponse> mudarStatus(@PathVariable Long id,
                                                         @RequestParam StatusMatricula status) {
        return ResponseEntity.ok(matriculaService.mudarStatusMatricula(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma matrícula (somente ADMIN)")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        matriculaService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}