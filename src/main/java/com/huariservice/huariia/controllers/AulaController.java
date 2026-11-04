package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.LessonRequest;
import com.huariservice.huariia.dto.LessonResponse;
import com.huariservice.huariia.services.LessonService;
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
@RequestMapping("/aulas")
@Tag(name = "Aulas")
@RequiredArgsConstructor
public class AulaController {

    private final LessonService lessonService;

    @PostMapping
    @Operation(summary = "Publica uma aula (somente ADMIN)")
    public ResponseEntity<LessonResponse> criar(@Valid @RequestBody LessonRequest request) {
        LessonResponse response = lessonService.createLesson(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista as aulas (use moduloId para filtrar por módulo, em ordem)")
    public ResponseEntity<List<LessonResponse>> listar(@RequestParam(required = false) Long moduloId) {
        List<LessonResponse> aulas = (moduloId != null)
                ? lessonService.listLessonsByModule(moduloId)
                : lessonService.listLessons();
        return ResponseEntity.ok(aulas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma aula pelo id")
    public ResponseEntity<LessonResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma aula (somente ADMIN)")
    public ResponseEntity<LessonResponse> atualizar(@PathVariable Long id,
                                                    @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.ok(lessonService.updateLesson(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma aula (somente ADMIN)")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        lessonService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
