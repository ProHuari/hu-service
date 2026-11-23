package com.huariservice.huariia.services;

import com.huariservice.huariia.config.CacheConfig;
import com.huariservice.huariia.dto.transparency.IntegrationsHealthDto;
import com.huariservice.huariia.dto.transparency.IntegrationsHealthDto.IntegrationItemStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Performs lightweight, cached reachability probes against configured external government APIs
 * (TCU, Camara, Senado, IBGE, ViaCEP) and checks Gemini API key availability.
 */
@Slf4j
@Service
public class ExternalIntegrationsMonitoringService {

    private static final int PROBE_TIMEOUT_MS = 2_000;

    private final GeminiIntegrationService geminiService;
    private final RestClient probeClient;
    private final String tcuBaseUrl;
    private final String camaraBaseUrl;
    private final String senadoBaseUrl;
    private final String ibgeBaseUrl;
    private final String viaCepBaseUrl;
    private final String geminiBaseUrl;

    public ExternalIntegrationsMonitoringService(
            GeminiIntegrationService geminiService,
            @Value("${app.audit-court.base-url:https://dadosabertos.tcu.gov.br/api}") String tcuBaseUrl,
            @Value("${app.camara.base-url:https://dadosabertos.camara.leg.br/api/v2}") String camaraBaseUrl,
            @Value("${app.senado.base-url:https://legis.senado.leg.br/dadosabertos}") String senadoBaseUrl,
            @Value("${app.ibge.base-url:https://servicodados.ibge.gov.br/api/v1/localidades}") String ibgeBaseUrl,
            @Value("${app.viacep.base-url:https://viacep.com.br/ws}") String viaCepBaseUrl,
            @Value("${app.ai.gemini.base-url:https://generativelanguage.googleapis.com/v1beta}") String geminiBaseUrl) {
        this.geminiService = geminiService;
        this.tcuBaseUrl = tcuBaseUrl;
        this.camaraBaseUrl = camaraBaseUrl;
        this.senadoBaseUrl = senadoBaseUrl;
        this.ibgeBaseUrl = ibgeBaseUrl;
        this.viaCepBaseUrl = viaCepBaseUrl;
        this.geminiBaseUrl = geminiBaseUrl;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(PROBE_TIMEOUT_MS);
        factory.setReadTimeout(PROBE_TIMEOUT_MS);
        this.probeClient = RestClient.builder()
                .requestFactory(factory)
                .defaultHeader("User-Agent", "Huari-Civic-Platform/1.0")
                .build();
    }

    @Cacheable(value = CacheConfig.INTEGRATIONS_HEALTH)
    public IntegrationsHealthDto checkHealth() {
        boolean geminiReal = geminiService.isApiKeyConfigured();

        String tcuStatus = probeReachability(tcuBaseUrl);
        String camaraStatus = probeReachability(camaraBaseUrl + "/deputados?itens=1");
        String senadoStatus = probeReachability(senadoBaseUrl + "/senador/lista/atual.json");
        String ibgeStatus = probeReachability(ibgeBaseUrl + "/estados");
        String viaCepStatus = probeReachability(viaCepBaseUrl + "/01001000/json");
        String geminiStatus = geminiReal ? "ONLINE" : "FALLBACK_SIMULATION";

        List<IntegrationItemStatusDto> items = List.of(
                new IntegrationItemStatusDto(
                        "tcu",
                        "Tribunal de Contas da União (TCU)",
                        "TRANSPARÊNCIA PÚBLICA",
                        tcuBaseUrl,
                        tcuStatus,
                        "Consultas a processos de fiscalização e registro de contas julgadas irregulares."
                ),
                new IntegrationItemStatusDto(
                        "camara",
                        "Câmara dos Deputados (Dados Abertos v2)",
                        "LEGISLATIVO FEDERAL",
                        camaraBaseUrl,
                        camaraStatus,
                        "Deputados federais, cotas parlamentares (CEAP) e tramitação de projetos de lei."
                ),
                new IntegrationItemStatusDto(
                        "senado",
                        "Senado Federal (Dados Abertos)",
                        "LEGISLATIVO FEDERAL",
                        senadoBaseUrl,
                        senadoStatus,
                        "Senadores em exercício e matérias legislativas em tramitação."
                ),
                new IntegrationItemStatusDto(
                        "ibge",
                        "IBGE Localidades",
                        "GEOGRAFIA E CIDADANIA",
                        ibgeBaseUrl,
                        ibgeStatus,
                        "Estados e municípios para filtros regionais de representantes e dados públicos."
                ),
                new IntegrationItemStatusDto(
                        "viacep",
                        "ViaCEP Webservice",
                        "GEOGRAFIA E CIDADANIA",
                        viaCepBaseUrl,
                        viaCepStatus,
                        "Resolução automática de CEP para vinculação do cidadão à sua bancada estadual."
                ),
                new IntegrationItemStatusDto(
                        "gemini",
                        "Google Gemini AI Generative API",
                        "INTELIGÊNCIA ARTIFICIAL",
                        geminiBaseUrl,
                        geminiStatus,
                        geminiReal
                                ? "Conectado ao modelo Gemini via API Key."
                                : "Modo de contingência inteligente ativo gerando análises didáticas locais."
                )
        );

        boolean anyDegraded = items.stream().anyMatch(i -> "DEGRADED".equals(i.status()));
        String overallStatus;
        if (anyDegraded) {
            overallStatus = "DEGRADED";
        } else if (!geminiReal) {
            overallStatus = "OPERATIONAL_WITH_FALLBACKS";
        } else {
            overallStatus = "HEALTHY";
        }

        return new IntegrationsHealthDto(LocalDateTime.now(), overallStatus, items);
    }

    private String probeReachability(String url) {
        try {
            probeClient.head()
                    .uri(url)
                    .retrieve()
                    .toBodilessEntity();
            return "ONLINE";
        } catch (RestClientResponseException ex) {
            // 4xx (e.g. 405 Method Not Allowed on HEAD) still proves the remote HTTP endpoint is reachable
            return ex.getStatusCode().is5xxServerError() ? "DEGRADED" : "ONLINE";
        } catch (Exception ex) {
            log.debug("Health probe degraded for {}: {}", url, ex.getMessage());
            return "DEGRADED";
        }
    }
}
