package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.ProgressoAulaRequest;
import com.huariservice.huariia.DTOs.ProgressoAulaResponse;
import com.huariservice.huariia.services.ProgressoAulaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
    @Operation(summary = "Registra o progresso de um usuário em uma aula (exige matrícula no curso)")
    public ResponseEntity<ProgressoAulaResponse> criar(@Valid @RequestBody ProgressoAulaRequest request) {
        ProgressoAulaResponse response = progressoAulaService.pubProgressoAula(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista os progressos (use usuarioId para filtrar por usuário)")
    public ResponseEntity<List<ProgressoAulaResponse>> listar(@RequestParam(required = false) Long usuarioId) {
        List<ProgressoAulaResponse> progressos = (usuarioId != null)
                ? progressoAulaService.mostrarProgressoDoUsuario(usuarioId)
                : progressoAulaService.mostrarProgressoAula();
        return ResponseEntity.ok(progressos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um progresso pelo id")
    public ResponseEntity<ProgressoAulaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(progressoAulaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza o status do progresso (usuário e aula não mudam)")
    public ResponseEntity<ProgressoAulaResponse> atualizar(@PathVariable Long id,
                                                           @Valid @RequestBody ProgressoAulaRequest request) {
        return ResponseEntity.ok(progressoAulaService.mudarProgressoAula(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um progresso")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        progressoAulaService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}