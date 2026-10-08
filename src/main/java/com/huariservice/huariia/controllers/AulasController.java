package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.AulaRequest;
import com.huariservice.huariia.DTOs.AulaResponse;
import com.huariservice.huariia.services.AulaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Aulas")
public class AulasController {
    private final AulaService aulaService;

    public AulasController(AulaService aulaService) {
        this.aulaService = aulaService;
    }

    @PostMapping
    public ResponseEntity<AulaResponse> publicarAula(@Valid @RequestBody AulaRequest request) {
        AulaResponse aula = aulaService.pubAula(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(aula);
    }
    @GetMapping
    public ResponseEntity<List<AulaResponse>> mostrarAulas() {

        List<AulaResponse> aulas = aulaService.mostrarAulas();

        return ResponseEntity.ok(aulas);
    }
    @PutMapping("/{id}")
    public ResponseEntity<AulaResponse> mudarAula(@PathVariable Long id, @Valid @RequestBody AulaRequest aulaAlterada) {
        AulaResponse aula = aulaService.mudarAula(id, aulaAlterada);
        return ResponseEntity.ok(aula);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAula(@PathVariable Long id) {
        aulaService.deletarId(id);
        return ResponseEntity.noContent().build();
    }

}
