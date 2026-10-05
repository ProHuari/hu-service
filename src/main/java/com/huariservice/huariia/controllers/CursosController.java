package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.CursoRequest;
import com.huariservice.huariia.DTOs.CursoResponse;
import com.huariservice.huariia.services.CursosService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Cursos")
public class CursosController {
    private final CursosService cursosService;

    public CursosController(CursosService cursosService) {
        this.cursosService = cursosService;
    }
    @PostMapping
    public ResponseEntity<CursoResponse> criarCurso(@Valid @RequestBody CursoRequest request) {
        CursoResponse novoCurso = cursosService.pubCurso(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCurso);
    }
    @GetMapping
    public ResponseEntity<List<CursoResponse>> listarCursos() {
        List<CursoResponse> cursos = cursosService.mostrarCursos();
        return ResponseEntity.ok(cursos);
    }
    @PutMapping("/{id}")
    public ResponseEntity<CursoResponse> atualizarCurso(@PathVariable Long id, @Valid @RequestBody CursoRequest request) {
        CursoResponse cursoAtualizado = cursosService.mudarCurso(id, request);
        return ResponseEntity.ok(cursoAtualizado);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCurso(@PathVariable Long id) {
        cursosService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}
