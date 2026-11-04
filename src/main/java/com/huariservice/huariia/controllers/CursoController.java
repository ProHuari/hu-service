package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.CourseRequest;
import com.huariservice.huariia.dto.CourseResponse;
import com.huariservice.huariia.services.CourseService;
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
@RequestMapping("/cursos")
@Tag(name = "Cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CourseService courseService;

    @PostMapping
    @Operation(summary = "Publica um curso")
    public ResponseEntity<CourseResponse> criar(@Valid @RequestBody CourseRequest request) {
        CourseResponse response = courseService.createCourse(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista os cursos")
    public ResponseEntity<List<CourseResponse>> listar() {
        return ResponseEntity.ok(courseService.listCourses());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um curso pelo id")
    public ResponseEntity<CourseResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um curso")
    public ResponseEntity<CourseResponse> atualizar(@PathVariable Long id,
                                                    @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um curso")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        courseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
