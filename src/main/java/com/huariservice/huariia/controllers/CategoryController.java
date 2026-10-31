package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.CategoryRequest;
import com.huariservice.huariia.DTOs.CategoryResponse;
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

/**
 * Controller for category management.
 */
@RestController
@RequestMapping("/categories")
@Tag(name = "Categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Creates a new category.
     *
     * @param request category creation data
     * @return the created category and location header
     */
    @PostMapping
    @Operation(summary = "Creates a new category")
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Lists all categories.
     *
     * @return list of categories
     */
    @GetMapping
    @Operation(summary = "Lists all categories")
    public ResponseEntity<List<CategoryResponse>> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    /**
     * Retrieves a category by ID.
     *
     * @param id category ID
     * @return category data
     */
    @GetMapping("/{id}")
    @Operation(summary = "Retrieves a category by ID")
    public ResponseEntity<CategoryResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    /**
     * Updates an existing category.
     *
     * @param id      category ID
     * @param request updated category data
     * @return updated category data
     */
    @PutMapping("/{id}")
    @Operation(summary = "Updates a category")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    /**
     * Deletes a category.
     *
     * @param id category ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes a category")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
