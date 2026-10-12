package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.CategoriaRequest;
import com.huariservice.huariia.DTOs.CategoriaResponse;
import com.huariservice.huariia.services.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Categorias")
public class CategoriasController {
    private final CategoriaService categoriaService;

    public CategoriasController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }
    @PostMapping
    public ResponseEntity<CategoriaResponse> criarCategoria(@Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse novaCategoria = categoriaService.pubCategoria(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
    }
    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listarCategorias() {
        List<CategoriaResponse> categorias = categoriaService.mostrarCategorias();
        return ResponseEntity.ok(categorias);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> atualizarCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse categoriaAtualizada = categoriaService.mudarCategoria(id, request);
        return ResponseEntity.ok(categoriaAtualizada);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCategoria(@PathVariable Long id) {
        categoriaService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}
