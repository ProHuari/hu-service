package com.huariservice.huariia.controller;

import com.huariservice.huariia.DTOs.Historico_IaRequest;
import com.huariservice.huariia.service.HistoricoIaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
