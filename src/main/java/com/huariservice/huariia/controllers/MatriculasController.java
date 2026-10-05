package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.MatriculasRequest;
import com.huariservice.huariia.DTOs.MatriculaResponse;
import com.huariservice.huariia.services.MatriculaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Matriculas")
public class MatriculasController {
    private final MatriculaService matriculaService;

    public MatriculasController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }
    @PostMapping
    public ResponseEntity<MatriculaResponse> criarMatricula(@Valid @RequestBody MatriculasRequest request) {
        MatriculaResponse novaMatricula = matriculaService.pubMatricula(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaMatricula);
    }
    @GetMapping
    public ResponseEntity<List<MatriculaResponse>> listarMatriculas() {
        List<MatriculaResponse> matriculas = matriculaService.mostrarMatriculas();
        return ResponseEntity.ok(matriculas);
    }
    @PutMapping("/{id}")
    public ResponseEntity<MatriculaResponse> atualizarMatricula(@PathVariable Long id, @Valid @RequestBody MatriculasRequest request) {
        MatriculaResponse matriculaAtualizada = matriculaService.mudarMatricula(id, request);
        return ResponseEntity.ok(matriculaAtualizada);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMatricula(@PathVariable Long id) {
        matriculaService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}
