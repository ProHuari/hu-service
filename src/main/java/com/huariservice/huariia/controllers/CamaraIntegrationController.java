package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.camara.CamaraDeputyDto;
import com.huariservice.huariia.dto.camara.CamaraExpenseDto;
import com.huariservice.huariia.dto.camara.CamaraPropositionDto;
import com.huariservice.huariia.dto.camara.CamaraSyncSummaryResponse;
import com.huariservice.huariia.services.CamaraIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transparencia/camara")
@Tag(name = "Camara dos Deputados Integration", description = "Real-time queries and synchronization for federal deputies, expenses, and propositions")
@RequiredArgsConstructor
public class CamaraIntegrationController {

    private final CamaraIntegrationService camaraService;

    @GetMapping("/deputados")
    @Operation(summary = "Searches federal deputies in real-time from Camara dos Deputados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deputies retrieved successfully")
    })
    public ResponseEntity<List<CamaraDeputyDto>> getDeputies(
            @RequestParam(name = "nome", required = false) String nome,
            @RequestParam(name = "uf", required = false) String uf,
            @RequestParam(name = "partido", required = false) String partido) {
        return ResponseEntity.ok(camaraService.fetchDeputies(nome, uf, partido));
    }

    @GetMapping("/deputados/{id}/despesas")
    @Operation(summary = "Queries parliamentary quota expenses (CEAP) of a deputy")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Expenses retrieved successfully")
    })
    public ResponseEntity<List<CamaraExpenseDto>> getDeputyExpenses(
            @PathVariable Long id,
            @RequestParam(name = "ano", required = false) Integer ano,
            @RequestParam(name = "mes", required = false) Integer mes) {
        return ResponseEntity.ok(camaraService.fetchDeputyExpenses(id, ano, mes));
    }

    @GetMapping("/proposicoes")
    @Operation(summary = "Queries legislative propositions and bills from Camara dos Deputados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Propositions retrieved successfully")
    })
    public ResponseEntity<List<CamaraPropositionDto>> getPropositions(
            @RequestParam(name = "siglaTipo", required = false, defaultValue = "PL") String siglaTipo,
            @RequestParam(name = "ano", required = false) Integer ano,
            @RequestParam(name = "ementa", required = false) String ementa) {
        return ResponseEntity.ok(camaraService.fetchPropositions(siglaTipo, ano, ementa));
    }

    @PostMapping("/sincronizar")
    @Operation(summary = "Synchronizes federal deputies into the local database (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deputies synchronized successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role")
    })
    public ResponseEntity<CamaraSyncSummaryResponse> syncDeputies(
            @RequestParam(name = "uf", required = false) String uf,
            @RequestParam(name = "limit", required = false, defaultValue = "50") Integer limit) {
        return ResponseEntity.ok(camaraService.syncDeputiesToDatabase(uf, limit));
    }
}
