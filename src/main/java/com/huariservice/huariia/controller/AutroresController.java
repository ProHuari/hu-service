package com.huariservice.huariia.controller;

import com.huariservice.huariia.DTOs.AutoresRequest;
import com.huariservice.huariia.service.AutoresService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/autores")
public class AutroresController {
    private final AutoresService autoresService;

    public AutroresController(AutoresService autoresService) {
        this.autoresService = autoresService;
    }


    @PostMapping
    public ResponseEntity<AutoresRequest> criarAutor(@RequestBody AutoresRequest request) {
        AutoresRequest novoAutor = autoresService.pubAutor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAutor);
    }
}
