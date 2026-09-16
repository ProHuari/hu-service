package com.huariservice.huariia.controller;

import com.huariservice.huariia.DTOs.ProgressoAulaRequest;
import com.huariservice.huariia.DTOs.ProgressoAulaResponse;
import com.huariservice.huariia.service.ProgressoAulaService;
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
    public ResponseEntity<ProgressoAulaRequest> criarProgressoAula(@RequestBody ProgressoAulaRequest request) {
        ProgressoAulaRequest novoProgresso = progressoAulaService.pubProgressoAula(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProgresso);
    }
    @GetMapping
    public ResponseEntity<List<ProgressoAulaResponse>> listarProgressoAula() {
        List<ProgressoAulaResponse> progressos = progressoAulaService.mostrarProgressoAula();
        return ResponseEntity.ok(progressos);
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> atualizarProgressoAula(@PathVariable Long id, @RequestBody ProgressoAulaRequest request) {
        String resposta = progressoAulaService.mudarProgressoAula(id, request);
        return ResponseEntity.ok(resposta);
    }
}
