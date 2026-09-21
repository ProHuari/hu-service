package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.AulasRequest;
import com.huariservice.huariia.DTOs.AulasResponse;
import com.huariservice.huariia.services.AulasService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aulas")
public class AulasController {
    private final AulasService aulasService;

    public AulasController(AulasService aulasService) {
        this.aulasService = aulasService;
    }


    @PostMapping
    public ResponseEntity<AulasResponse> publicarAula(@Valid @RequestBody AulasRequest request) {
        AulasResponse aula = aulasService.pubAula(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(aula);
    }
    @GetMapping
    public ResponseEntity<List<AulasResponse>> mostrarAulas() {

        List<AulasResponse> aulas = aulasService.mostrarAulas();

        return ResponseEntity.ok(aulas);
    }
    @PutMapping("/{id}")
    public ResponseEntity<AulasResponse> mudarAula(@PathVariable Long id, @Valid @RequestBody AulasRequest aulaAlterada) {
        AulasResponse aula = aulasService.mudarAula(id, aulaAlterada);
        return ResponseEntity.ok(aula);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAula(@PathVariable Long id) {
        aulasService.deletarId(id);
        return ResponseEntity.noContent().build();
    }

}
