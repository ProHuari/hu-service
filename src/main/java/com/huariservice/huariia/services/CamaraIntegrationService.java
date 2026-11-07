package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.camara.*;
import com.huariservice.huariia.entities.Parliamentarian;
import com.huariservice.huariia.entities.enums.ParliamentarianChamber;
import com.huariservice.huariia.repositories.ParliamentarianRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service orchestrating integration with the Camara dos Deputados Open Data API (v2)
 * for deputies, public expenses, and legislative propositions, including database synchronization.
 */
@Slf4j
@Service
public class CamaraIntegrationService {

    private final RestClient restClient;
    private final ParliamentarianRepository parliamentarianRepository;
    private final String baseUrl;

    public CamaraIntegrationService(
            @Qualifier("camaraRestClient") RestClient restClient,
            ParliamentarianRepository parliamentarianRepository,
            @Value("${app.camara.base-url:https://dadosabertos.camara.leg.br/api/v2}") String baseUrl) {
        this.restClient = restClient;
        this.parliamentarianRepository = parliamentarianRepository;
        this.baseUrl = baseUrl;
    }

    /**
     * Searches federal deputies in real-time by name, state (UF), and party with defensive fallback.
     */
    public List<CamaraDeputyDto> fetchDeputies(String name, String state, String party) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/deputados")
                    .queryParam("ordem", "ASC")
                    .queryParam("ordenarPor", "nome");

            if (name != null && !name.isBlank()) {
                builder.queryParam("nome", name.trim());
            }
            if (state != null && !state.isBlank()) {
                builder.queryParam("siglaUf", state.trim().toUpperCase());
            }
            if (party != null && !party.isBlank()) {
                builder.queryParam("siglaPartido", party.trim().toUpperCase());
            }

            CamaraWrapperResponse<CamaraDeputyDto> response = restClient.get()
                    .uri(builder.build().toUriString())
                    .retrieve()
                    .body(new ParameterizedTypeReference<CamaraWrapperResponse<CamaraDeputyDto>>() {});

            return response != null && response.dados() != null ? response.dados() : Collections.emptyList();
        } catch (RestClientResponseException ex) {
            log.warn("Camara API returned HTTP status {}: {}", ex.getStatusCode().value(), ex.getMessage());
            return Collections.emptyList();
        } catch (ResourceAccessException ex) {
            log.warn("Timeout or network error connecting to Camara API: {}", ex.getMessage());
            return Collections.emptyList();
        } catch (Exception ex) {
            log.warn("Unexpected error reading deputies from Camara API: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Searches parliamentary quota expenses (CEAP) for a specific deputy.
     */
    public List<CamaraExpenseDto> fetchDeputyExpenses(Long deputyId, Integer year, Integer month) {
        if (deputyId == null) {
            return Collections.emptyList();
        }
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/deputados/{id}/despesas")
                    .queryParam("ordem", "DESC")
                    .queryParam("ordenarPor", "dataDocumento");

            if (year != null && year > 0) {
                builder.queryParam("ano", year);
            }
            if (month != null && month >= 1 && month <= 12) {
                builder.queryParam("mes", month);
            }

            CamaraWrapperResponse<CamaraExpenseDto> response = restClient.get()
                    .uri(builder.buildAndExpand(deputyId).toUriString())
                    .retrieve()
                    .body(new ParameterizedTypeReference<CamaraWrapperResponse<CamaraExpenseDto>>() {});

            return response != null && response.dados() != null ? response.dados() : Collections.emptyList();
        } catch (RestClientResponseException ex) {
            log.warn("Camara API expenses returned HTTP status {}: {}", ex.getStatusCode().value(), ex.getMessage());
            return Collections.emptyList();
        } catch (ResourceAccessException ex) {
            log.warn("Timeout connecting to Camara API expenses: {}", ex.getMessage());
            return Collections.emptyList();
        } catch (Exception ex) {
            log.warn("Unexpected error reading expenses from Camara API: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Searches legislative propositions (e.g. PL, PEC) from Camara dos Deputados.
     */
    public List<CamaraPropositionDto> fetchPropositions(String siglaTipo, Integer year, String keyword) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/proposicoes")
                    .queryParam("ordem", "DESC")
                    .queryParam("ordenarPor", "ano");

            String type = (siglaTipo != null && !siglaTipo.isBlank()) ? siglaTipo.trim().toUpperCase() : "PL";
            builder.queryParam("siglaTipo", type);

            if (year != null && year > 0) {
                builder.queryParam("ano", year);
            }
            if (keyword != null && !keyword.isBlank()) {
                builder.queryParam("ementa", keyword.trim());
            }

            CamaraWrapperResponse<CamaraPropositionDto> response = restClient.get()
                    .uri(builder.build().toUriString())
                    .retrieve()
                    .body(new ParameterizedTypeReference<CamaraWrapperResponse<CamaraPropositionDto>>() {});

            return response != null && response.dados() != null ? response.dados() : Collections.emptyList();
        } catch (RestClientResponseException ex) {
            log.warn("Camara API propositions returned HTTP status {}: {}", ex.getStatusCode().value(), ex.getMessage());
            return Collections.emptyList();
        } catch (ResourceAccessException ex) {
            log.warn("Timeout connecting to Camara API propositions: {}", ex.getMessage());
            return Collections.emptyList();
        } catch (Exception ex) {
            log.warn("Unexpected error reading propositions from Camara API: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Synchronizes real deputies from the Camara API into the local database, avoiding duplicates.
     */
    @Transactional
    public CamaraSyncSummaryResponse syncDeputiesToDatabase(String state, Integer limit) {
        try {
            List<CamaraDeputyDto> remoteDeputies = fetchDeputies(null, state, null);
            if (remoteDeputies.isEmpty()) {
                return CamaraSyncSummaryResponse.fallback(
                        "Nenhum parlamentar foi retornado pela API da Câmara para sincronização (ou a API externa está instável).");
            }

            int maxItems = (limit != null && limit > 0) ? Math.min(limit, remoteDeputies.size()) : remoteDeputies.size();
            List<CamaraDeputyDto> targetList = remoteDeputies.subList(0, maxItems);

            int created = 0;
            int updated = 0;

            for (CamaraDeputyDto remote : targetList) {
                String externalId = String.valueOf(remote.id());
                Optional<Parliamentarian> existingOpt = parliamentarianRepository
                        .findByExternalIdAndChamber(externalId, ParliamentarianChamber.CHAMBER_OF_DEPUTIES);

                Parliamentarian entity;
                if (existingOpt.isPresent()) {
                    entity = existingOpt.get();
                    updated++;
                } else {
                    entity = new Parliamentarian();
                    entity.setExternalId(externalId);
                    entity.setChamber(ParliamentarianChamber.CHAMBER_OF_DEPUTIES);
                    created++;
                }

                entity.setName(remote.nome() != null ? remote.nome().trim() : "Nome Indisponível");
                entity.setParty(remote.siglaPartido() != null && !remote.siglaPartido().isBlank()
                        ? remote.siglaPartido().trim().toUpperCase() : "S/PARTIDO");
                entity.setState(remote.siglaUf() != null && !remote.siglaUf().isBlank()
                        ? remote.siglaUf().trim().toUpperCase() : "BR");

                parliamentarianRepository.save(entity);
            }

            return CamaraSyncSummaryResponse.success(targetList.size(), created, updated);
        } catch (Exception ex) {
            log.error("Failed to sync deputies with local database: {}", ex.getMessage(), ex);
            return CamaraSyncSummaryResponse.fallback(
                    "Falha ao sincronizar dados com o banco local: " + ex.getMessage());
        }
    }
}
