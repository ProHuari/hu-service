package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.CourseModuleRequest;
import com.huariservice.huariia.dto.CourseModuleResponse;
import com.huariservice.huariia.services.CourseModuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/modulos")
@Tag(name = "Módulos")
@RequiredArgsConstructor
public class ModuloController {

    private final CourseModuleService courseModuleService;

    @PostMapping
    @Operation(summary = "Cadastra um módulo")
    public ResponseEntity<CourseModuleResponse> criar(@Valid @RequestBody CourseModuleRequest request) {
        CourseModuleResponse response = courseModuleService.createModule(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista os módulos (use cursoId para filtrar por curso, em ordem)")
    public ResponseEntity<List<CourseModuleResponse>> listar(@RequestParam(required = false) Long cursoId) {
        List<CourseModuleResponse> modulos = (cursoId != null)
                ? courseModuleService.listModulesByCourse(cursoId)
                : courseModuleService.listModules();
        return ResponseEntity.ok(modulos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um módulo pelo id")
    public ResponseEntity<CourseModuleResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(courseModuleService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um módulo")
    public ResponseEntity<CourseModuleResponse> atualizar(@PathVariable Long id,
                                                          @Valid @RequestBody CourseModuleRequest request) {
        return ResponseEntity.ok(courseModuleService.updateModule(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um módulo")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        courseModuleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
