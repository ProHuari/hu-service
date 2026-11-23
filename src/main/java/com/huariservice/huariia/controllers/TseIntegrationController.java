package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.tse.TseCandidateAssetsResponse;
import com.huariservice.huariia.dto.tse.TseCandidateSummaryDto;
import com.huariservice.huariia.services.TseIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transparencia/tse")
@Tag(name = "TSE DivulgaCandContas Integration", description = "Public electoral data and declared asset evolution of candidates and parliamentarians from Tribunal Superior Eleitoral")
@RequiredArgsConstructor
public class TseIntegrationController {

    private final TseIntegrationService tseService;

    @GetMapping("/candidatos")
    @Operation(summary = "Lists electoral candidates from TSE DivulgaCandContas with optional filters by name, state, year, and office")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Candidates retrieved successfully")
    })
    public ResponseEntity<List<TseCandidateSummaryDto>> listCandidates(
            @RequestParam(name = "nome", required = false) String nome,
            @RequestParam(name = "uf", required = false, defaultValue = "SP") String uf,
            @RequestParam(name = "ano", required = false, defaultValue = "2022") Integer ano,
            @RequestParam(name = "cargo", required = false) String cargo) {
        return ResponseEntity.ok(tseService.fetchCandidates(nome, uf, ano, cargo));
    }

    @GetMapping("/candidatos/{id}/bens")
    @Operation(summary = "Returns declared assets and consolidated total value for a candidate from TSE DivulgaCandContas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Candidate assets retrieved successfully")
    })
    public ResponseEntity<TseCandidateAssetsResponse> getCandidateAssets(
            @PathVariable Long id,
            @RequestParam(name = "ano", required = false, defaultValue = "2022") Integer ano,
            @RequestParam(name = "uf", required = false, defaultValue = "SP") String uf) {
        return ResponseEntity.ok(tseService.fetchCandidateAssets(id, ano, uf));
    }
}
