package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.AuthorRequest;
import com.huariservice.huariia.DTOs.AuthorResponse;
import com.huariservice.huariia.services.AuthorService;
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
 * Controller for author operations and catalog management.
 */
@RestController
@RequestMapping("/authors")
@Tag(name = "Authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    /**
     * Creates a new author.
     *
     * @param request author creation data
     * @return the created author and location header
     */
    @PostMapping
    @Operation(summary = "Creates a new author")
    public ResponseEntity<AuthorResponse> create(@Valid @RequestBody AuthorRequest request) {
        AuthorResponse response = authorService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Lists all authors.
     *
     * @return list of authors
     */
    @GetMapping
    @Operation(summary = "Lists all authors")
    public ResponseEntity<List<AuthorResponse>> findAll() {
        return ResponseEntity.ok(authorService.findAll());
    }

    /**
     * Retrieves an author by ID.
     *
     * @param id author ID
     * @return author data
     */
    @GetMapping("/{id}")
    @Operation(summary = "Retrieves an author by ID")
    public ResponseEntity<AuthorResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.findById(id));
    }

    /**
     * Updates an author.
     *
     * @param id      author ID
     * @param request updated author data
     * @return updated author data
     */
    @PutMapping("/{id}")
    @Operation(summary = "Updates an author")
    public ResponseEntity<AuthorResponse> update(@PathVariable Long id,
            @Valid @RequestBody AuthorRequest request) {
        return ResponseEntity.ok(authorService.update(id, request));
    }

    /**
     * Deletes an author.
     *
     * @param id author ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes an author")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
