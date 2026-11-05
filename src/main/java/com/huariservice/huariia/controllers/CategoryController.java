package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.CategoryRequest;
import com.huariservice.huariia.dto.CategoryResponse;
import com.huariservice.huariia.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/categorias")
@Tag(name = "Categories", description = "Course category management")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Creates a new category (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Category name already exists")
    })
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lists categories with pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated list of categories"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<CategoryResponse>> listCategories(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(categoryService.listCategories(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Finds a category by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<CategoryResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Updates a category (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Category name already exists")
    })
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id,
                                                           @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes (soft-delete) a category by ID (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deleted"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
