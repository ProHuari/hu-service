package com.huariservice.huariia.controller;

import com.huariservice.huariia.DTOs.Historico_IaRequest;
import com.huariservice.huariia.DTOs.Historico_IaResponse;
import com.huariservice.huariia.service.HistoricoIaService;
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
    public ResponseEntity<Historico_IaRequest> criarHistorico(@RequestBody Historico_IaRequest request) {
        Historico_IaRequest novoHistorico = historicoIaService.pubHistoricoIa(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoHistorico);
    }
    @GetMapping
    public ResponseEntity<List<Historico_IaResponse>> listarHistoricos() {
        List<Historico_IaResponse> historicos = historicoIaService.mostrarHistoricoIa();
        return ResponseEntity.ok(historicos);
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> atualizarHistorico(@PathVariable Long id, @RequestBody Historico_IaRequest request) {
        String resposta = historicoIaService.mudarHistoricoIa(id, request);
        return ResponseEntity.ok(resposta);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarHistorico(@PathVariable Long id) {
        String resposta = historicoIaService.deletarId(id);

        if (resposta.equals("Esse histórico não foi registrado")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
        }

        return ResponseEntity.ok(resposta);
    }
}
