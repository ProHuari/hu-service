package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.CategoriaRequest;
import com.huariservice.huariia.dto.CategoriaResponse;
import com.huariservice.huariia.services.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/categorias")
@Tag(name = "Categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;


    @PostMapping
    @Operation(summary = "Cadastra uma categoria")
    public ResponseEntity<CategoriaResponse> criar(@Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse response = categoriaService.pubCategoria(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista as categorias")
    public ResponseEntity<List<CategoriaResponse>> listar() {
        return ResponseEntity.ok(categoriaService.mostrarCategorias());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma categoria pelo id")
    public ResponseEntity<CategoriaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma categoria")
    public ResponseEntity<CategoriaResponse> atualizar(@PathVariable Long id,
                                                       @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(categoriaService.mudarCategoria(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma categoria")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        categoriaService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}