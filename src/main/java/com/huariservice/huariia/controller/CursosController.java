package com.huariservice.huariia.controller;

import com.huariservice.huariia.DTOs.CursosRequest;
import com.huariservice.huariia.service.CursosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cursos")
public class CursosController {
    private final CursosService cursosService;

    public CursosController(CursosService cursosService) {
        this.cursosService = cursosService;
    }
    @PostMapping
    public ResponseEntity<CursosRequest> criarCurso(@RequestBody CursosRequest request) {
        CursosRequest novoCurso = cursosService.pubCurso(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCurso);
    }
}
