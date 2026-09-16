package com.huariservice.huariia.controller;

import com.huariservice.huariia.DTOs.ModulosRequest;
import com.huariservice.huariia.service.ModulosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
