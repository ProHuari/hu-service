package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.LessonProgressRequest;
import com.huariservice.huariia.dto.LessonProgressResponse;
import com.huariservice.huariia.security.DadosToken;
import com.huariservice.huariia.services.LessonProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/progresso-aulas")
@Tag(name = "Progresso das aulas")
@RequiredArgsConstructor
public class ProgressoAulaController {

    private final LessonProgressService lessonProgressService;

    @PostMapping
    @Operation(summary = "Registra o progresso do usuário logado em uma aula (exige matrícula no curso)")
    public ResponseEntity<LessonProgressResponse> criar(
            @Valid @RequestBody LessonProgressRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        LessonProgressResponse response =
                lessonProgressService.createProgress(usuarioLogado.usuarioId(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Aluno vê o próprio progresso; admin vê todos (filtro opcional usuarioId)")
    public ResponseEntity<List<LessonProgressResponse>> listar(
            @RequestParam(required = false) Long usuarioId,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        List<LessonProgressResponse> progressos;
        if (!usuarioLogado.isAdmin()) {
            progressos = lessonProgressService.listProgressByUser(usuarioLogado.usuarioId());
        } else if (usuarioId != null) {
            progressos = lessonProgressService.listProgressByUser(usuarioId);
        } else {
            progressos = lessonProgressService.listProgress();
        }
        return ResponseEntity.ok(progressos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um progresso pelo id (aluno só acessa o próprio)")
    public ResponseEntity<LessonProgressResponse> buscar(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        return ResponseEntity.ok(
                lessonProgressService.findById(id, usuarioLogado.usuarioId(), usuarioLogado.isAdmin()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza o status do progresso (usuário e aula não mudam)")
    public ResponseEntity<LessonProgressResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody LessonProgressRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        return ResponseEntity.ok(lessonProgressService.updateProgress(
                id, request, usuarioLogado.usuarioId(), usuarioLogado.isAdmin()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um progresso (aluno só remove o próprio)")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        lessonProgressService.deleteById(id, usuarioLogado.usuarioId(), usuarioLogado.isAdmin());
        return ResponseEntity.noContent().build();
    }
}
