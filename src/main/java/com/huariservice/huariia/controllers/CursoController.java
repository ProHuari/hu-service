package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.CursoRequest;
import com.huariservice.huariia.dto.CursoResponse;
import com.huariservice.huariia.services.CursoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cursos")
@Tag(name = "Cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;


    @PostMapping
    @Operation(summary = "Publica um curso")
    public ResponseEntity<CursoResponse> criar(@Valid @RequestBody CursoRequest request) {
        CursoResponse response = cursoService.pubCurso(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista os cursos")
    public ResponseEntity<List<CursoResponse>> listar() {
        return ResponseEntity.ok(cursoService.mostrarCursos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um curso pelo id")
    public ResponseEntity<CursoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(cursoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um curso")
    public ResponseEntity<CursoResponse> atualizar(@PathVariable Long id,
                                                   @Valid @RequestBody CursoRequest request) {
        return ResponseEntity.ok(cursoService.mudarCurso(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um curso")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        cursoService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}