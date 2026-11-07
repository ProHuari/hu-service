package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.senado.SenadoMatterDto;
import com.huariservice.huariia.dto.senado.SenadoSenatorDto;
import com.huariservice.huariia.services.SenadoIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transparencia/senado")
@Tag(name = "Senado Federal Integration", description = "Real-time queries for federal senators and legislative matters from the Federal Senate")
@RequiredArgsConstructor
public class SenadoIntegrationController {

    private final SenadoIntegrationService senadoService;

    @GetMapping("/senadores")
    @Operation(summary = "Lists active federal senators in real-time from the Federal Senate")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Senators retrieved successfully")
    })
    public ResponseEntity<List<SenadoSenatorDto>> getSenators(
            @RequestParam(name = "uf", required = false) String uf,
            @RequestParam(name = "partido", required = false) String partido) {
        return ResponseEntity.ok(senadoService.fetchCurrentSenators(uf, partido));
    }

    @GetMapping("/materias")
    @Operation(summary = "Queries legislative matters and bills from the Federal Senate")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Legislative matters retrieved successfully")
    })
    public ResponseEntity<List<SenadoMatterDto>> getMatters(
            @RequestParam(name = "sigla", required = false, defaultValue = "PL") String sigla,
            @RequestParam(name = "ano", required = false) Integer ano) {
        return ResponseEntity.ok(senadoService.fetchMatters(sigla, ano));
    }
}
