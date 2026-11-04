package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.CategoryRequest;
import com.huariservice.huariia.dto.CategoryResponse;
import com.huariservice.huariia.services.CategoryService;
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
@RequestMapping("/categorias")
@Tag(name = "Categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Cadastra uma categoria")
    public ResponseEntity<CategoryResponse> criar(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista as categorias")
    public ResponseEntity<List<CategoryResponse>> listar() {
        return ResponseEntity.ok(categoryService.listCategories());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma categoria pelo id")
    public ResponseEntity<CategoryResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma categoria")
    public ResponseEntity<CategoryResponse> atualizar(@PathVariable Long id,
                                                      @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma categoria")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
