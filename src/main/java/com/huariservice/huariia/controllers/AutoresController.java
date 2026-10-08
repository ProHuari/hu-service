package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.AutorRequest;
import com.huariservice.huariia.DTOs.AutorResponse;
import com.huariservice.huariia.services.AutorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Autores")
public class AutoresController {

    private final AutorService autorService;

    public AutoresController(AutorService autorService) {
        this.autorService = autorService;
    }

    @PostMapping
    public ResponseEntity<AutorResponse> criarAutor(@RequestBody AutorRequest request) {
        AutorResponse novoAutor = autorService.pubAutor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAutor);
    }
    @GetMapping
    public ResponseEntity<List<AutorResponse>> listarAutores() {
        List<AutorResponse> autores = autorService.mostrarAutores();
        return ResponseEntity.ok(autores);
    }
    @GetMapping("/{id}")
    public ResponseEntity<AutorResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(autorService.buscarPorId(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<AutorResponse> atualizarAutor(@PathVariable Long id, @Valid @RequestBody AutorRequest request) {
        AutorResponse autorAtualizado = autorService.mudarAutor(id, request);
        return ResponseEntity.ok(autorAtualizado);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAutor(@PathVariable Long id) {
        autorService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}
