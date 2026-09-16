package com.huariservice.huariia.controller;

import com.huariservice.huariia.DTOs.AutoresRequest;
import com.huariservice.huariia.DTOs.AutoresResponse;
import com.huariservice.huariia.service.AutoresService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/autores")
public class AutroresController {
    private final AutoresService autoresService;

    public AutroresController(AutoresService autoresService) {
        this.autoresService = autoresService;
    }

    @PostMapping
    public ResponseEntity<AutoresRequest> criarAutor(@RequestBody AutoresRequest request) {
        AutoresRequest novoAutor = autoresService.pubAutor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAutor);
    }
    @GetMapping
    public ResponseEntity<List<AutoresResponse>> listarAutores() {
        List<AutoresResponse> autores = autoresService.mostrarAutores();
        return ResponseEntity.ok(autores);
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> atualizarAutor(@PathVariable Long id, @RequestBody AutoresRequest request) {
        String resposta = autoresService.mudarAutor(id, request);
        return ResponseEntity.ok(resposta);
    }
}
