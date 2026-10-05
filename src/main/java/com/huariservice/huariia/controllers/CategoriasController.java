package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.CategoriasRequest;
import com.huariservice.huariia.DTOs.CategoriaResponse;
import com.huariservice.huariia.services.CategoriasService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Categorias")
public class CategoriasController {
    private final CategoriasService categoriasService;

    public CategoriasController(CategoriasService categoriasService) {
        this.categoriasService = categoriasService;
    }
    @PostMapping
    public ResponseEntity<CategoriaResponse> criarCategoria(@Valid @RequestBody CategoriasRequest request) {
        CategoriaResponse novaCategoria = categoriasService.pubCategoria(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
    }
    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listarCategorias() {
        List<CategoriaResponse> categorias = categoriasService.mostrarCategorias();
        return ResponseEntity.ok(categorias);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriasService.buscarPorId(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> atualizarCategoria(@PathVariable Long id, @Valid @RequestBody CategoriasRequest request) {
        CategoriaResponse categoriaAtualizada = categoriasService.mudarCategoria(id, request);
        return ResponseEntity.ok(categoriaAtualizada);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCategoria(@PathVariable Long id) {
        categoriasService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}
