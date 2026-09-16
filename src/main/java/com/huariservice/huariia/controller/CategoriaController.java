package com.huariservice.huariia.controller;

import com.huariservice.huariia.DTOs.CategoriasRequest;
import com.huariservice.huariia.DTOs.CategoriasResponse;
import com.huariservice.huariia.service.CategoriasService;
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
    public ResponseEntity<CategoriasRequest> criarCategoria(@RequestBody CategoriasRequest request) {
        CategoriasRequest novaCategoria = categoriasService.pubCategoria(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
    }
    @GetMapping
    public ResponseEntity<List<CategoriasResponse>> listarCategorias() {
        List<CategoriasResponse> categorias = categoriasService.mostrarCategorias();
        return ResponseEntity.ok(categorias);
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> atualizarCategoria(@PathVariable Long id, @RequestBody CategoriasRequest request) {
        String resposta = categoriasService.mudarCategoria(id, request);
        return ResponseEntity.ok(resposta);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarCategoria(@PathVariable Long id) {
        String resposta = categoriasService.deletarId(id);

        if (resposta.equals("Essa categoria não foi cadastrada")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
        }

        return ResponseEntity.ok(resposta);
    }
}
