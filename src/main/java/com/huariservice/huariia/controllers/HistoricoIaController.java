package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.HistoricoIaRequest;
import com.huariservice.huariia.DTOs.HistoricoIaResponse;
import com.huariservice.huariia.services.HistoricoIaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/HistoricoIA")
public class HistoricoIaController {
    private final HistoricoIaService historicoIaService;


    public HistoricoIaController(HistoricoIaService historicoIaService) {
        this.historicoIaService = historicoIaService;
    }

    @PostMapping
    public ResponseEntity<HistoricoIaResponse> criarHistorico(@Valid @RequestBody HistoricoIaRequest request) {
        HistoricoIaResponse novoHistorico = historicoIaService.pubHistoricoIa(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoHistorico);
    }
    @GetMapping
    public ResponseEntity<List<HistoricoIaResponse>> listarHistoricos() {
        List<HistoricoIaResponse> historicos = historicoIaService.mostrarHistoricoIa();
        return ResponseEntity.ok(historicos);
    }
    @PutMapping("/{id}")
    public ResponseEntity<HistoricoIaResponse> atualizarHistorico(@PathVariable Long id, @Valid @RequestBody HistoricoIaRequest request) {
        HistoricoIaResponse historicoAtualizado = historicoIaService.mudarHistoricoIa(id, request);
        return ResponseEntity.ok(historicoAtualizado);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarHistorico(@PathVariable Long id) {
        historicoIaService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}
