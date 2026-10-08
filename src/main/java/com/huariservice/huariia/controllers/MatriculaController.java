package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.MatriculaRequest;
import com.huariservice.huariia.DTOs.MatriculaResponse;
import com.huariservice.huariia.entities.enums.StatusMatricula;
import com.huariservice.huariia.services.MatriculaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/matriculas")
@Tag(name = "Matrículas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @PostMapping
    @Operation(summary = "Matricula um usuário em um curso")
    public ResponseEntity<MatriculaResponse> criar(@Valid @RequestBody MatriculaRequest request) {
        MatriculaResponse response = matriculaService.pubMatricula(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista as matrículas (use usuarioId para filtrar por usuário)")
    public ResponseEntity<List<MatriculaResponse>> listar(@RequestParam(required = false) Long usuarioId) {
        List<MatriculaResponse> matriculas = (usuarioId != null)
                ? matriculaService.mostrarMatriculasDoUsuario(usuarioId)
                : matriculaService.mostrarMatriculas();
        return ResponseEntity.ok(matriculas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma matrícula pelo id")
    public ResponseEntity<MatriculaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(matriculaService.buscarPorId(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Altera o status da matrícula (ATIVA, CONCLUIDA ou CANCELADA)")
    public ResponseEntity<MatriculaResponse> mudarStatus(@PathVariable Long id,
                                                         @RequestParam StatusMatricula status) {
        return ResponseEntity.ok(matriculaService.mudarStatusMatricula(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma matrícula")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        matriculaService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}