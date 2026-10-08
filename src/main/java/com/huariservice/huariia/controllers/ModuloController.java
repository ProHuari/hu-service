package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.ModuloRequest;
import com.huariservice.huariia.DTOs.ModuloResponse;
import com.huariservice.huariia.services.ModuloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/modulos")
@Tag(name = "Módulos")
public class ModuloController {

    private final ModuloService moduloService;

    public ModuloController(ModuloService moduloService) {
        this.moduloService = moduloService;
    }

    @PostMapping
    @Operation(summary = "Cadastra um módulo")
    public ResponseEntity<ModuloResponse> criar(@Valid @RequestBody ModuloRequest request) {
        ModuloResponse response = moduloService.pubModulo(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista os módulos (use cursoId para filtrar por curso, em ordem)")
    public ResponseEntity<List<ModuloResponse>> listar(@RequestParam(required = false) Long cursoId) {
        List<ModuloResponse> modulos = (cursoId != null)
                ? moduloService.mostrarModulosDoCurso(cursoId)
                : moduloService.mostrarModulos();
        return ResponseEntity.ok(modulos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um módulo pelo id")
    public ResponseEntity<ModuloResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(moduloService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um módulo")
    public ResponseEntity<ModuloResponse> atualizar(@PathVariable Long id,
                                                    @Valid @RequestBody ModuloRequest request) {
        return ResponseEntity.ok(moduloService.mudarModulo(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um módulo")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        moduloService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}