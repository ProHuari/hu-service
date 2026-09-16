package com.huariservice.huariia.controller;

import com.huariservice.huariia.DTOs.AulasRequest;
import com.huariservice.huariia.service.AulasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
