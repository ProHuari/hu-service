package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.AulaRequest;
import com.huariservice.huariia.DTOs.AulaResponse;
import com.huariservice.huariia.services.AulaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/aulas")
@Tag(name = "Aulas")
public class AulaController {

    private final AulaService aulaService;

    public AulaController(AulaService aulaService) {
        this.aulaService = aulaService;
    }

    @PostMapping
    @Operation(summary = "Publica uma aula")
    public ResponseEntity<AulaResponse> criar(@Valid @RequestBody AulaRequest request) {
        AulaResponse response = aulaService.pubAula(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista as aulas (use moduloId para filtrar por módulo, em ordem)")
    public ResponseEntity<List<AulaResponse>> listar(@RequestParam(required = false) Long moduloId) {
        List<AulaResponse> aulas = (moduloId != null)
                ? aulaService.mostrarAulasDoModulo(moduloId)
                : aulaService.mostrarAulas();
        return ResponseEntity.ok(aulas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma aula pelo id")
    public ResponseEntity<AulaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(aulaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma aula")
    public ResponseEntity<AulaResponse> atualizar(@PathVariable Long id,
                                                  @Valid @RequestBody AulaRequest request) {
        return ResponseEntity.ok(aulaService.mudarAula(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma aula")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        aulaService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}