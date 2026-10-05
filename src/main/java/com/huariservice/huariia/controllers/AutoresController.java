package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.AutoresRequest;
import com.huariservice.huariia.DTOs.AutorResponse;
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
    public ResponseEntity<AutorResponse> criarAutor(@RequestBody AutoresRequest request) {
        AutorResponse novoAutor = autoresService.pubAutor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAutor);
    }
    @GetMapping
    public ResponseEntity<List<AutorResponse>> listarAutores() {
        List<AutorResponse> autores = autoresService.mostrarAutores();
        return ResponseEntity.ok(autores);
    }
    @GetMapping("/{id}")
    public ResponseEntity<AutorResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(autoresService.buscarPorId(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<AutorResponse> atualizarAutor(@PathVariable Long id, @Valid @RequestBody AutoresRequest request) {
        AutorResponse autorAtualizado = autoresService.mudarAutor(id, request);
        return ResponseEntity.ok(autorAtualizado);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAutor(@PathVariable Long id) {
        autoresService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}
