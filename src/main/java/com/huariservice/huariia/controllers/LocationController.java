package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.location.CepLookupResponse;
import com.huariservice.huariia.dto.location.MunicipalityResponse;
import com.huariservice.huariia.dto.location.StateResponse;
import com.huariservice.huariia.services.LocationIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/localidades")
@Tag(name = "Regional Citizenship & Locations (IBGE / ViaCEP)", description = "Geographic endpoints for Brazilian states, municipalities, and postal code lookups")
@RequiredArgsConstructor
public class LocationController {

    private final LocationIntegrationService locationService;

    @GetMapping("/estados")
    @Operation(summary = "Lists all Brazilian federative states (UFs) from IBGE for frontend filtering")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "States list retrieved successfully")
    })
    public ResponseEntity<List<StateResponse>> getStates() {
        return ResponseEntity.ok(locationService.getStates());
    }

    @GetMapping("/estados/{uf}/municipios")
    @Operation(summary = "Lists all municipalities within a Brazilian state from IBGE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Municipalities retrieved successfully")
    })
    public ResponseEntity<List<MunicipalityResponse>> getMunicipalities(@PathVariable String uf) {
        return ResponseEntity.ok(locationService.getMunicipalities(uf));
    }

    @GetMapping("/cep/{cep}")
    @Operation(summary = "Looks up address and state (UF) from ViaCEP to automatically filter regional representatives")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Postal code details retrieved successfully")
    })
    public ResponseEntity<CepLookupResponse> lookupCep(@PathVariable String cep) {
        return ResponseEntity.ok(locationService.lookupCep(cep));
    }
}
