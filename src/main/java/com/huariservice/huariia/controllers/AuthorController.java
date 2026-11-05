package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.AuthorRequest;
import com.huariservice.huariia.dto.AuthorResponse;
import com.huariservice.huariia.services.AuthorService;
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
@RequestMapping("/autores")
@Tag(name = "Authors", description = "Course author / content creator management")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    @Operation(summary = "Creates a new author (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Author created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Channel name or URL already exists")
    })
    public ResponseEntity<AuthorResponse> createAuthor(@Valid @RequestBody AuthorRequest request) {
        AuthorResponse response = authorService.createAuthor(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lists authors with pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated list of authors"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<AuthorResponse>> listAuthors(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(authorService.listAuthors(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Finds an author by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Author found"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<AuthorResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Updates an author (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Author updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Author not found"),
            @ApiResponse(responseCode = "409", description = "Channel name or URL already exists")
    })
    public ResponseEntity<AuthorResponse> updateAuthor(@PathVariable Long id,
                                                       @Valid @RequestBody AuthorRequest request) {
        return ResponseEntity.ok(authorService.updateAuthor(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes (soft-delete) an author by ID (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Author deleted"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
