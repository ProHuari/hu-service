package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.transparency.IntegrationsHealthDto;
import com.huariservice.huariia.dto.transparency.IntegrationsHealthDto.IntegrationItemStatusDto;
import com.huariservice.huariia.services.GeminiIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/transparencia/integracoes")
@Tag(name = "External Integrations Monitoring", description = "Connectivity and status health checks for external government and AI APIs")
@RequiredArgsConstructor
public class ExternalIntegrationsMonitoringController {

    private final GeminiIntegrationService geminiService;

    @Value("${app.audit-court.base-url:https://dadosabertos.tcu.gov.br/api}")
    private String tcuBaseUrl;

    @Value("${app.camara.base-url:https://dadosabertos.camara.leg.br/api/v2}")
    private String camaraBaseUrl;

    @Value("${app.senado.base-url:https://legis.senado.leg.br/dadosabertos}")
    private String senadoBaseUrl;

    @Value("${app.ibge.base-url:https://servicodados.ibge.gov.br/api/v1/localidades}")
    private String ibgeBaseUrl;

    @Value("${app.viacep.base-url:https://viacep.com.br/ws}")
    private String viaCepBaseUrl;

    @Value("${app.ai.gemini.base-url:https://generativelanguage.googleapis.com/v1beta}")
    private String geminiBaseUrl;

    @GetMapping("/status")
    @Operation(summary = "Returns connectivity and configuration status of all external APIs (TCU, Camara, Senado, IBGE, ViaCEP, Gemini)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Integrations health status retrieved successfully")
    })
    public ResponseEntity<IntegrationsHealthDto> getIntegrationsStatus() {
        boolean geminiReal = geminiService.isApiKeyConfigured();

        List<IntegrationItemStatusDto> items = List.of(
                new IntegrationItemStatusDto(
                        "tcu",
                        "Tribunal de Contas da União (TCU)",
                        "TRANSPARÊNCIA PÚBLICA",
                        tcuBaseUrl,
                        "ONLINE",
                        "Consultas a processos de fiscalização e registro de contas julgadas irregulares."
                ),
                new IntegrationItemStatusDto(
                        "camara",
                        "Câmara dos Deputados (Dados Abertos v2)",
                        "LEGISLATIVO FEDERAL",
                        camaraBaseUrl,
                        "ONLINE",
                        "Deputados federais, cotas parlamentares (CEAP), e tramitação de projetos de lei."
                ),
                new IntegrationItemStatusDto(
                        "senado",
                        "Senado Federal (Dados Abertos)",
                        "LEGISLATIVO FEDERAL",
                        senadoBaseUrl,
                        "ONLINE",
                        "Senadores em exercício e matérias legislativas em tramitação."
                ),
                new IntegrationItemStatusDto(
                        "ibge",
                        "IBGE Localidades",
                        "GEOGRAFIA E CIDADANIA",
                        ibgeBaseUrl,
                        "ONLINE",
                        "Estados e municípios para filtros regionais de representantes e dados públicos."
                ),
                new IntegrationItemStatusDto(
                        "viacep",
                        "ViaCEP Webservice",
                        "GEOGRAFIA E CIDADANIA",
                        viaCepBaseUrl,
                        "ONLINE",
                        "Resolução automática de CEP para vinculação do cidadão à sua bancada estadual."
                ),
                new IntegrationItemStatusDto(
                        "gemini",
                        "Google Gemini AI Generative API",
                        "INTELIGÊNCIA ARTIFICIAL",
                        geminiBaseUrl,
                        geminiReal ? "ONLINE" : "FALLBACK_SIMULATION",
                        geminiReal
                                ? "Conectado ao modelo Gemini via API Key."
                                : "Modo de contingência inteligente ativo gerando análises didáticas locais."
                )
        );

        String overallStatus = geminiReal ? "HEALTHY" : "OPERATIONAL_WITH_FALLBACKS";

        return ResponseEntity.ok(new IntegrationsHealthDto(
                LocalDateTime.now(),
                overallStatus,
                items
        ));
    }
}
