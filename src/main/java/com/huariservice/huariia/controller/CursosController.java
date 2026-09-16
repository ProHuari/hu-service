package com.huariservice.huariia.controller;

import com.huariservice.huariia.DTOs.CursosRequest;
import com.huariservice.huariia.DTOs.CursosResponse;
import com.huariservice.huariia.service.CursosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping
    public ResponseEntity<List<CursosResponse>> listarCursos() {
        List<CursosResponse> cursos = cursosService.mostrarCursos();
        return ResponseEntity.ok(cursos);
    }
}
