package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.AutorRequest;
import com.huariservice.huariia.DTOs.AutorResponse;
import com.huariservice.huariia.services.AutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/autores")
@Tag(name = "Autores")
public class AutorController {

    private final AutorService autorService;

    public AutorController(AutorService autorService) {
        this.autorService = autorService;
    }

    @PostMapping
    @Operation(summary = "Cadastra um autor")
    public ResponseEntity<AutorResponse> criar(@Valid @RequestBody AutorRequest request) {
        AutorResponse response = autorService.pubAutor(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista os autores")
    public ResponseEntity<List<AutorResponse>> listar() {
        return ResponseEntity.ok(autorService.mostrarAutores());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um autor pelo id")
    public ResponseEntity<AutorResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(autorService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um autor")
    public ResponseEntity<AutorResponse> atualizar(@PathVariable Long id,
                                                   @Valid @RequestBody AutorRequest request) {
        return ResponseEntity.ok(autorService.mudarAutor(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um autor")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        autorService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}