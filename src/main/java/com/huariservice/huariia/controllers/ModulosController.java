package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.ModuloRequest;
import com.huariservice.huariia.DTOs.ModuloResponse;
import com.huariservice.huariia.services.ModuloService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Modulos")
public class ModulosController {
    private final ModuloService moduloService;

    public ModulosController(ModuloService moduloService) {
        this.moduloService = moduloService;
    }
    @PostMapping
    public ResponseEntity<ModuloResponse> criarModulo(@Valid @RequestBody ModuloRequest request) {
        ModuloResponse novoModulo = moduloService.pubModulo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoModulo);
    }
    @GetMapping
    public ResponseEntity<List<ModuloResponse>> listarModulos() {
        List<ModuloResponse> modulos = moduloService.mostrarModulos();
        return ResponseEntity.ok(modulos);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ModuloResponse> atualizarModulo(@PathVariable Long id, @Valid @RequestBody ModuloRequest request) {
        ModuloResponse moduloAtualizado = moduloService.mudarModulo(id, request);
        return ResponseEntity.ok(moduloAtualizado);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarModulo(@PathVariable Long id) {
        moduloService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}
