package com.huariservice.huariia.controller;

import com.huariservice.huariia.DTOs.ModulosRequest;
import com.huariservice.huariia.DTOs.ModulosResponse;
import com.huariservice.huariia.service.ModulosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/modulos")
public class ModulosController {
    private final ModulosService modulosService;

    public ModulosController(ModulosService modulosService) {
        this.modulosService = modulosService;
    }
    @PostMapping
    public ResponseEntity<ModulosRequest> criarModulo(@RequestBody ModulosRequest request) {
        ModulosRequest novoModulo = modulosService.pubModulo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoModulo);
    }
    @GetMapping
    public ResponseEntity<List<ModulosResponse>> listarModulos() {
        List<ModulosResponse> modulos = modulosService.mostrarModulos();
        return ResponseEntity.ok(modulos);
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> atualizarModulo(@PathVariable Long id, @RequestBody ModulosRequest request) {
        String resposta = modulosService.mudarModulo(id, request);
        return ResponseEntity.ok(resposta);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarModulo(@PathVariable Long id) {
        String resposta = modulosService.deletarId(id);

        if (resposta.equals("Esse módulo não foi cadastrado")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
        }

        return ResponseEntity.ok(resposta);
    }
}
