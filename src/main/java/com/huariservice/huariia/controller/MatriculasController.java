package com.huariservice.huariia.controller;

import com.huariservice.huariia.DTOs.MatriculasRequest;
import com.huariservice.huariia.service.MatriculaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
