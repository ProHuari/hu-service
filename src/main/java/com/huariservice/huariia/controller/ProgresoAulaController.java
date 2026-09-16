package com.huariservice.huariia.controller;

import com.huariservice.huariia.DTOs.ProgressoAulaRequest;
import com.huariservice.huariia.service.ProgressoAulaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
