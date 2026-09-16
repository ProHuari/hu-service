package com.huariservice.huariia.controller;

import com.huariservice.huariia.DTOs.MatriculasRequest;
import com.huariservice.huariia.DTOs.MatriculasResponse;
import com.huariservice.huariia.service.MatriculaService;
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
    public ResponseEntity<MatriculasRequest> criarMatricula(@RequestBody MatriculasRequest request) {
        MatriculasRequest novaMatricula = matriculaService.pubMatricula(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaMatricula);
    }
    @GetMapping
    public ResponseEntity<List<MatriculasResponse>> listarMatriculas() {
        List<MatriculasResponse> matriculas = matriculaService.mostrarMatriculas();
        return ResponseEntity.ok(matriculas);
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> atualizarMatricula(@PathVariable Long id, @RequestBody MatriculasRequest request) {
        String resposta = matriculaService.mudarMatricula(id, request);
        return ResponseEntity.ok(resposta);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarMatricula(@PathVariable Long id) {
        String resposta = matriculaService.deletarId(id);

        if (resposta.equals("Essa matrícula não foi realizada")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
        }

        return ResponseEntity.ok(resposta);
    }
}
