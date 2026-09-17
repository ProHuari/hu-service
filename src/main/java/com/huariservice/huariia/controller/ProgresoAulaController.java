package com.huariservice.huariia.controller;

import com.huariservice.huariia.DTOs.ProgressoAulaRequest;
import com.huariservice.huariia.DTOs.ProgressoAulaResponse;
import com.huariservice.huariia.service.ProgressoAulaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ProgresoAula")
public class ProgresoAulaController {
    private final ProgressoAulaService progressoAulaService;

    public ProgresoAulaController(ProgressoAulaService progressoAulaService) {
        this.progressoAulaService = progressoAulaService;
    }

    @PostMapping
    public ResponseEntity<ProgressoAulaResponse> criarProgressoAula(@Valid @RequestBody ProgressoAulaRequest request) {
        ProgressoAulaResponse novoProgresso = progressoAulaService.pubProgressoAula(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProgresso);
    }
    @GetMapping
    public ResponseEntity<List<ProgressoAulaResponse>> listarProgressoAula() {
        List<ProgressoAulaResponse> progressos = progressoAulaService.mostrarProgressoAula();
        return ResponseEntity.ok(progressos);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProgressoAulaResponse> atualizarProgressoAula(@PathVariable Long id, @Valid @RequestBody ProgressoAulaRequest request) {
        ProgressoAulaResponse progressoAtualizado = progressoAulaService.mudarProgressoAula(id, request);
        return ResponseEntity.ok(progressoAtualizado);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProgressoAula(@PathVariable Long id) {
        progressoAulaService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}
