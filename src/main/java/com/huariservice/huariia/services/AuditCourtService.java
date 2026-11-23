package com.huariservice.huariia.services;

import com.huariservice.huariia.config.CacheConfig;
import com.huariservice.huariia.dto.auditcourt.AuditCourtIrregularAccountResponse;
import com.huariservice.huariia.dto.auditcourt.AuditCourtProcessResponse;
import com.huariservice.huariia.dto.auditcourt.AuditCourtSummaryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

/**
 * Service orchestrating external communication with the Tribunal de Contas (TCU / TCE)
 * Open Data API, equipped with resilient timeouts and defensive fallback strategies.
 */
@Slf4j
@Service
public class AuditCourtService {

    private static final String PROVIDER_NAME = "Tribunal de Contas da União (TCU) / TCE";

    private final RestClient restClient;
    private final String baseUrl;

    public AuditCourtService(
            @Qualifier("auditCourtRestClient") RestClient restClient,
            @Value("${app.audit-court.base-url:https://dadosabertos.tcu.gov.br/api}") String baseUrl) {
        this.restClient = restClient;
        this.baseUrl = baseUrl;
    }

    /**
     * Consolidates transparency queries from the Tribunal de Contas with graceful fallback.
     *
     * @param query optional search keyword or parliamentarian name
     * @return summary response containing status and retrieved items
     */
    @Cacheable(value = CacheConfig.AUDIT_COURT_OVERVIEW, unless = "#result == null || 'FALLBACK'.equals(#result.status())")
    public AuditCourtSummaryResponse fetchTransparencyOverview(String query) {
        try {
            List<AuditCourtProcessResponse> processes = fetchProcessesOrThrow(query, 10);
            List<AuditCourtIrregularAccountResponse> irregularAccounts = fetchIrregularAccountsOrThrow(query);

            return AuditCourtSummaryResponse.success(
                    PROVIDER_NAME,
                    processes,
                    irregularAccounts,
                    baseUrl
            );
        } catch (Exception ex) {
            log.warn("Tribunal de Contas external service failure: {}", ex.getMessage());
            return AuditCourtSummaryResponse.fallback(
                    PROVIDER_NAME,
                    "A API do Tribunal de Contas está temporariamente indisponível. Exibindo dados locais de contingência.",
                    baseUrl
            );
        }
    }

    /**
     * Queries audit and oversight processes with defensive fallback.
     *
     * @param subject keyword filter
     * @param limit maximum items
     * @return list of processes or empty list on failure
     */
    @Cacheable(value = CacheConfig.AUDIT_COURT_PROCESSES, unless = "#result == null || #result.isEmpty()")
    public List<AuditCourtProcessResponse> fetchProcesses(String subject, Integer limit) {
        try {
            return fetchProcessesOrThrow(subject, limit);
        } catch (RestClientResponseException ex) {
            log.warn("Audit Court API returned HTTP status {}: {}", ex.getStatusCode().value(), ex.getMessage());
            return Collections.emptyList();
        } catch (ResourceAccessException ex) {
            log.warn("Timeout or network error connecting to Audit Court API: {}", ex.getMessage());
            return Collections.emptyList();
        } catch (Exception ex) {
            log.warn("Unexpected error reading processes from Audit Court API: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    private List<AuditCourtProcessResponse> fetchProcessesOrThrow(String subject, Integer limit) {
        int maxResults = (limit != null && limit > 0) ? Math.min(limit, 50) : 20;
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/processo/recupera-processos")
                .queryParam("quantidade", maxResults);
        if (subject != null && !subject.isBlank()) {
            builder.queryParam("termo", subject.trim());
        }

        List<AuditCourtProcessResponse> response = restClient.get()
                .uri(builder.build().toUriString())
                .retrieve()
                .body(new ParameterizedTypeReference<List<AuditCourtProcessResponse>>() {});

        return response != null ? response : Collections.emptyList();
    }

    /**
     * Queries irregular public accounts register with defensive fallback.
     *
     * @param nameOrCpf keyword, name, or document
     * @return list of irregular accounts or empty list on failure
     */
    @Cacheable(value = CacheConfig.AUDIT_COURT_IRREGULAR_ACCOUNTS, unless = "#result == null || #result.isEmpty()")
    public List<AuditCourtIrregularAccountResponse> fetchIrregularAccounts(String nameOrCpf) {
        try {
            return fetchIrregularAccountsOrThrow(nameOrCpf);
        } catch (RestClientResponseException ex) {
            log.warn("Audit Court API (irregular accounts) returned HTTP status {}: {}",
                    ex.getStatusCode().value(), ex.getMessage());
            return Collections.emptyList();
        } catch (ResourceAccessException ex) {
            log.warn("Timeout connecting to Audit Court API (irregular accounts): {}", ex.getMessage());
            return Collections.emptyList();
        } catch (Exception ex) {
            log.warn("Unexpected error reading irregular accounts from Audit Court API: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    private List<AuditCourtIrregularAccountResponse> fetchIrregularAccountsOrThrow(String nameOrCpf) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/contas-irregulares");
        if (nameOrCpf != null && !nameOrCpf.isBlank()) {
            builder.queryParam("nome", nameOrCpf.trim());
        } else {
            builder.queryParam("quantidade", 10);
        }

        List<AuditCourtIrregularAccountResponse> response = restClient.get()
                .uri(builder.build().toUriString())
                .retrieve()
                .body(new ParameterizedTypeReference<List<AuditCourtIrregularAccountResponse>>() {});

        return response != null ? response : Collections.emptyList();
    }
}

