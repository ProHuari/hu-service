package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.ModulosRequest;
import com.huariservice.huariia.DTOs.ModulosResponse;
import com.huariservice.huariia.services.ModulosService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Modulos")
public class ModulosController {
    private final ModulosService modulosService;

    public ModulosController(ModulosService modulosService) {
        this.modulosService = modulosService;
    }
    @PostMapping
    public ResponseEntity<ModulosResponse> criarModulo(@Valid @RequestBody ModulosRequest request) {
        ModulosResponse novoModulo = modulosService.pubModulo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoModulo);
    }
    @GetMapping
    public ResponseEntity<List<ModulosResponse>> listarModulos() {
        List<ModulosResponse> modulos = modulosService.mostrarModulos();
        return ResponseEntity.ok(modulos);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ModulosResponse> atualizarModulo(@PathVariable Long id, @Valid @RequestBody ModulosRequest request) {
        ModulosResponse moduloAtualizado = modulosService.mudarModulo(id, request);
        return ResponseEntity.ok(moduloAtualizado);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarModulo(@PathVariable Long id) {
        modulosService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}
