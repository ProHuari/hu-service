package com.huariservice.huariia.controller;

import com.huariservice.huariia.DTOs.Historico_IaRequest;
import com.huariservice.huariia.DTOs.Historico_IaResponse;
import com.huariservice.huariia.service.HistoricoIaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historicoIA")
public class HistoricoIaController {
    private final HistoricoIaService historicoIaService;


    public HistoricoIaController(HistoricoIaService historicoIaService) {
        this.historicoIaService = historicoIaService;
    }

    @PostMapping
    public ResponseEntity<Historico_IaResponse> criarHistorico(@Valid @RequestBody Historico_IaRequest request) {
        Historico_IaResponse novoHistorico = historicoIaService.pubHistoricoIa(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoHistorico);
    }
    @GetMapping
    public ResponseEntity<List<Historico_IaResponse>> listarHistoricos() {
        List<Historico_IaResponse> historicos = historicoIaService.mostrarHistoricoIa();
        return ResponseEntity.ok(historicos);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Historico_IaResponse> atualizarHistorico(@PathVariable Long id, @Valid @RequestBody Historico_IaRequest request) {
        Historico_IaResponse historicoAtualizado = historicoIaService.mudarHistoricoIa(id, request);
        return ResponseEntity.ok(historicoAtualizado);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarHistorico(@PathVariable Long id) {
        historicoIaService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}
