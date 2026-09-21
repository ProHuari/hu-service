package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.AutoresRequest;
import com.huariservice.huariia.DTOs.AutoresResponse;
import com.huariservice.huariia.services.AutoresService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Autores")
public class AutoresController {

    private final AutoresService autoresService;

    public AutoresController(AutoresService autoresService) {
        this.autoresService = autoresService;
    }

    @PostMapping
    public ResponseEntity<AutoresResponse> criarAutor(@RequestBody AutoresRequest request) {
        AutoresResponse novoAutor = autoresService.pubAutor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAutor);
    }
    @GetMapping
    public ResponseEntity<List<AutoresResponse>> listarAutores() {
        List<AutoresResponse> autores = autoresService.mostrarAutores();
        return ResponseEntity.ok(autores);
    }
    @PutMapping("/{id}")
    public ResponseEntity<AutoresResponse> atualizarAutor(@PathVariable Long id, @Valid @RequestBody AutoresRequest request) {
        AutoresResponse autorAtualizado = autoresService.mudarAutor(id, request);
        return ResponseEntity.ok(autorAtualizado);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAutor(@PathVariable Long id) {
        autoresService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}
