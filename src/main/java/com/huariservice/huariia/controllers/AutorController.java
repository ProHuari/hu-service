package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.AuthorRequest;
import com.huariservice.huariia.dto.AuthorResponse;
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

@RestController
@RequestMapping("/autores")
@Tag(name = "Autores")
@RequiredArgsConstructor
public class AutorController {

    private final AuthorService authorService;

    @PostMapping
    @Operation(summary = "Cadastra um autor")
    public ResponseEntity<AuthorResponse> criar(@Valid @RequestBody AuthorRequest request) {
        AuthorResponse response = authorService.createAuthor(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista os autores")
    public ResponseEntity<List<AuthorResponse>> listar() {
        return ResponseEntity.ok(authorService.listAuthors());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um autor pelo id")
    public ResponseEntity<AuthorResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um autor")
    public ResponseEntity<AuthorResponse> atualizar(@PathVariable Long id,
                                                    @Valid @RequestBody AuthorRequest request) {
        return ResponseEntity.ok(authorService.updateAuthor(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um autor")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        authorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
