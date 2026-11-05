package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.EnrollmentRequest;
import com.huariservice.huariia.dto.EnrollmentResponse;
import com.huariservice.huariia.entities.enums.EnrollmentStatus;
import com.huariservice.huariia.security.DadosToken;
import com.huariservice.huariia.services.EnrollmentService;
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
@RequestMapping("/matriculas")
@Tag(name = "Matrículas")
@RequiredArgsConstructor
public class MatriculaController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @Operation(summary = "Matricula o usuário logado em um curso")
    public ResponseEntity<EnrollmentResponse> criar(
            @Valid @RequestBody EnrollmentRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        EnrollmentResponse response = enrollmentService.enroll(usuarioLogado.usuarioId(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Aluno vê as próprias matrículas; admin vê todas (filtro opcional usuarioId)")
    public ResponseEntity<List<EnrollmentResponse>> listar(
            @RequestParam(required = false) Long usuarioId,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        List<EnrollmentResponse> matriculas;
        if (!usuarioLogado.isAdmin()) {
            matriculas = enrollmentService.listEnrollmentsByUser(usuarioLogado.usuarioId());
        } else if (usuarioId != null) {
            matriculas = enrollmentService.listEnrollmentsByUser(usuarioId);
        } else {
            matriculas = enrollmentService.listEnrollments();
        }
        return ResponseEntity.ok(matriculas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma matrícula pelo id (aluno só acessa as próprias)")
    public ResponseEntity<EnrollmentResponse> buscar(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        return ResponseEntity.ok(
                enrollmentService.findById(id, usuarioLogado.usuarioId(), usuarioLogado.isAdmin()));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Altera o status da matrícula (somente ADMIN)")
    public ResponseEntity<EnrollmentResponse> mudarStatus(@PathVariable Long id,
                                                          @RequestParam EnrollmentStatus status) {
        return ResponseEntity.ok(enrollmentService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma matrícula (somente ADMIN)")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        enrollmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
