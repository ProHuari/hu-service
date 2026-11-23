package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.transparency.IntegrationsHealthDto;
import com.huariservice.huariia.services.ExternalIntegrationsMonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transparencia/integracoes")
@Tag(name = "External Integrations Monitoring", description = "Cached connectivity and availability health checks for external government and AI APIs")
@RequiredArgsConstructor
public class ExternalIntegrationsMonitoringController {

    private final ExternalIntegrationsMonitoringService monitoringService;

    @GetMapping("/status")
    @Operation(summary = "Returns cached connectivity and availability status of all external APIs (TCU, Camara, Senado, IBGE, ViaCEP, Gemini)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Integrations health status retrieved successfully")
    })
    public ResponseEntity<IntegrationsHealthDto> getIntegrationsStatus() {
        return ResponseEntity.ok(monitoringService.checkHealth());
    }
}
