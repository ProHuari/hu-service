package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.cnpj.CnpjCompanyResponse;
import com.huariservice.huariia.services.CnpjIntegrationService;
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

@RestController
@RequestMapping("/transparencia/empresas")
@Tag(name = "Company Transparency Integration (BrasilAPI / CNPJ)", description = "Public company registration, corporate structure (QSA), and civic risk alerts from BrasilAPI")
@RequiredArgsConstructor
public class CompanyTransparencyController {

    private final CnpjIntegrationService cnpjService;

    @GetMapping("/{cnpj}")
    @Operation(summary = "Queries public company registration data, partners (QSA), and transparency alerts by CNPJ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company data retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid CNPJ format")
    })
    public ResponseEntity<CnpjCompanyResponse> getCompanyByCnpj(@PathVariable String cnpj) {
        return ResponseEntity.ok(cnpjService.fetchCompanyByCnpj(cnpj));
    }
}
