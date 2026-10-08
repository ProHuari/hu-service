package com.huariservice.huariia.controllers;

import com.huariservice.huariia.DTOs.HistoricoIaRequest;
import com.huariservice.huariia.DTOs.HistoricoIaResponse;
import com.huariservice.huariia.services.HistoricoIaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

// O histórico é somente leitura: não há PUT.
@RestController
@RequestMapping("/historico-ia")
@Tag(name = "Histórico de IA")
public class HistoricoIaController {

    private final HistoricoIaService historicoIaService;

    public HistoricoIaController(HistoricoIaService historicoIaService) {
        this.historicoIaService = historicoIaService;
    }

    @PostMapping
    @Operation(summary = "Registra uma consulta à IA")
    public ResponseEntity<HistoricoIaResponse> criar(@Valid @RequestBody HistoricoIaRequest request) {
        HistoricoIaResponse response = historicoIaService.pubHistoricoIa(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista o histórico (use usuarioId para filtrar, mais recentes primeiro)")
    public ResponseEntity<List<HistoricoIaResponse>> listar(@RequestParam(required = false) Long usuarioId) {
        List<HistoricoIaResponse> historico = (usuarioId != null)
                ? historicoIaService.mostrarHistoricoDoUsuario(usuarioId)
                : historicoIaService.mostrarHistoricoIa();
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um registro do histórico pelo id")
    public ResponseEntity<HistoricoIaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(historicoIaService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um registro do histórico")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        historicoIaService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}