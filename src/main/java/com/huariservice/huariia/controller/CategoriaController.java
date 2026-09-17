package com.huariservice.huariia.controller;

import com.huariservice.huariia.DTOs.CategoriasRequest;
import com.huariservice.huariia.DTOs.CategoriasResponse;
import com.huariservice.huariia.service.CategoriasService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categoria")
public class CategoriaController {
    private final CategoriasService categoriasService;

    public CategoriaController(CategoriasService categoriasService) {
        this.categoriasService = categoriasService;
    }
    @PostMapping
    public ResponseEntity<CategoriasResponse> criarCategoria(@Valid @RequestBody CategoriasRequest request) {
        CategoriasResponse novaCategoria = categoriasService.pubCategoria(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
    }
    @GetMapping
    public ResponseEntity<List<CategoriasResponse>> listarCategorias() {
        List<CategoriasResponse> categorias = categoriasService.mostrarCategorias();
        return ResponseEntity.ok(categorias);
    }
    @PutMapping("/{id}")
    public ResponseEntity<CategoriasResponse> atualizarCategoria(@PathVariable Long id, @Valid @RequestBody CategoriasRequest request) {
        CategoriasResponse categoriaAtualizada = categoriasService.mudarCategoria(id, request);
        return ResponseEntity.ok(categoriaAtualizada);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCategoria(@PathVariable Long id) {
        categoriasService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}
