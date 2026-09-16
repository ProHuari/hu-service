package com.huariservice.huariia.controller;

import com.huariservice.huariia.DTOs.AulasRequest;
import com.huariservice.huariia.DTOs.AulasResponse;
import com.huariservice.huariia.service.AulasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("aulas")
public class aulasservice {
    private final AulasService aulasService;

    public aulasservice(AulasService aulasService) {
        this.aulasService = aulasService;
    }


    @PostMapping
    public ResponseEntity<AulasRequest> publicarAula(
            @RequestBody AulasRequest request) {

        AulasRequest aula = aulasService.pubAula(request);

        return ResponseEntity.ok(aula);
    }
    @GetMapping
    public ResponseEntity<List<AulasResponse>> mostrarAulas() {

        List<AulasResponse> aulas = aulasService.mostrarAulas();

        return ResponseEntity.ok(aulas);
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> mudarAula(
            @PathVariable Long id,
            @RequestBody AulasRequest aulaAlterada) {

        String resposta = aulasService.mudarAula(id, aulaAlterada);

        return ResponseEntity.ok(resposta);
    }


}
