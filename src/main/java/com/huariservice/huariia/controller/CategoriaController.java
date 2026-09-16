package com.huariservice.huariia.controller;

import com.huariservice.huariia.DTOs.CategoriasRequest;
import com.huariservice.huariia.service.CategoriasService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
