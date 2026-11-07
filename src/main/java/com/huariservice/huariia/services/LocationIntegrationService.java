package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.location.CepLookupResponse;
import com.huariservice.huariia.dto.location.MunicipalityResponse;
import com.huariservice.huariia.dto.location.StateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Service managing geographic integrations with IBGE and ViaCEP for regional citizenship
 * and localization features.
 */
@Slf4j
@Service
public class LocationIntegrationService {

    private final RestClient ibgeRestClient;
    private final RestClient viaCepRestClient;
    private final String ibgeBaseUrl;
    private final String viaCepBaseUrl;

    public LocationIntegrationService(
            @Qualifier("ibgeRestClient") RestClient ibgeRestClient,
            @Qualifier("viaCepRestClient") RestClient viaCepRestClient,
            @Value("${app.ibge.base-url:https://servicodados.ibge.gov.br/api/v1/localidades}") String ibgeBaseUrl,
            @Value("${app.viacep.base-url:https://viacep.com.br/ws}") String viaCepBaseUrl) {
        this.ibgeRestClient = ibgeRestClient;
        this.viaCepRestClient = viaCepRestClient;
        this.ibgeBaseUrl = ibgeBaseUrl;
        this.viaCepBaseUrl = viaCepBaseUrl;
    }

    /**
     * Lists all Brazilian federative states (UFs) ordered alphabetically by name.
     */
    public List<StateResponse> getStates() {
        try {
            List<StateResponse> response = ibgeRestClient.get()
                    .uri("/estados?orderBy=nome")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<StateResponse>>() {});

            if (response != null && !response.isEmpty()) {
                return response;
            }
            return fallbackStates();
        } catch (RestClientResponseException ex) {
            log.warn("IBGE states API returned HTTP status {}: {}", ex.getStatusCode().value(), ex.getMessage());
            return fallbackStates();
        } catch (ResourceAccessException ex) {
            log.warn("Timeout connecting to IBGE states API: {}", ex.getMessage());
            return fallbackStates();
        } catch (Exception ex) {
            log.warn("Unexpected error reading states from IBGE: {}", ex.getMessage());
            return fallbackStates();
        }
    }

    /**
     * Lists municipalities of a specific Brazilian state (UF).
     */
    public List<MunicipalityResponse> getMunicipalities(String uf) {
        if (uf == null || uf.trim().length() != 2) {
            return Collections.emptyList();
        }
        try {
            List<MunicipalityResponse> response = ibgeRestClient.get()
                    .uri("/estados/{uf}/municipios", uf.trim().toUpperCase())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<MunicipalityResponse>>() {});

            return response != null ? response : Collections.emptyList();
        } catch (RestClientResponseException ex) {
            log.warn("IBGE municipalities API returned HTTP status {}: {}", ex.getStatusCode().value(), ex.getMessage());
            return Collections.emptyList();
        } catch (ResourceAccessException ex) {
            log.warn("Timeout connecting to IBGE municipalities API: {}", ex.getMessage());
            return Collections.emptyList();
        } catch (Exception ex) {
            log.warn("Unexpected error reading municipalities from IBGE: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Looks up address and federative state (UF) information by postal code (CEP).
     */
    public CepLookupResponse lookupCep(String rawCep) {
        if (rawCep == null || rawCep.isBlank()) {
            return CepLookupResponse.fallback("");
        }
        String cleanCep = rawCep.replaceAll("\\D", "");
        if (cleanCep.length() != 8) {
            return CepLookupResponse.fallback(cleanCep);
        }

        try {
            CepLookupResponse response = viaCepRestClient.get()
                    .uri("/{cep}/json", cleanCep)
                    .retrieve()
                    .body(CepLookupResponse.class);

            if (response != null && !response.hasError()) {
                return response;
            }
            return CepLookupResponse.fallback(cleanCep);
        } catch (RestClientResponseException ex) {
            log.warn("ViaCEP API returned HTTP status {}: {}", ex.getStatusCode().value(), ex.getMessage());
            return CepLookupResponse.fallback(cleanCep);
        } catch (ResourceAccessException ex) {
            log.warn("Timeout connecting to ViaCEP API: {}", ex.getMessage());
            return CepLookupResponse.fallback(cleanCep);
        } catch (Exception ex) {
            log.warn("Unexpected error querying ViaCEP API: {}", ex.getMessage());
            return CepLookupResponse.fallback(cleanCep);
        }
    }

    private List<StateResponse> fallbackStates() {
        return List.of(
                new StateResponse(12L, "AC", "Acre", new StateResponse.RegionDto(1L, "N", "Norte")),
                new StateResponse(27L, "AL", "Alagoas", new StateResponse.RegionDto(2L, "NE", "Nordeste")),
                new StateResponse(16L, "AP", "Amapá", new StateResponse.RegionDto(1L, "N", "Norte")),
                new StateResponse(13L, "AM", "Amazonas", new StateResponse.RegionDto(1L, "N", "Norte")),
                new StateResponse(29L, "BA", "Bahia", new StateResponse.RegionDto(2L, "NE", "Nordeste")),
                new StateResponse(23L, "CE", "Ceará", new StateResponse.RegionDto(2L, "NE", "Nordeste")),
                new StateResponse(53L, "DF", "Distrito Federal", new StateResponse.RegionDto(5L, "CO", "Centro-Oeste")),
                new StateResponse(32L, "ES", "Espírito Santo", new StateResponse.RegionDto(3L, "SE", "Sudeste")),
                new StateResponse(52L, "GO", "Goiás", new StateResponse.RegionDto(5L, "CO", "Centro-Oeste")),
                new StateResponse(21L, "MA", "Maranhão", new StateResponse.RegionDto(2L, "NE", "Nordeste")),
                new StateResponse(51L, "MT", "Mato Grosso", new StateResponse.RegionDto(5L, "CO", "Centro-Oeste")),
                new StateResponse(50L, "MS", "Mato Grosso do Sul", new StateResponse.RegionDto(5L, "CO", "Centro-Oeste")),
                new StateResponse(31L, "MG", "Minas Gerais", new StateResponse.RegionDto(3L, "SE", "Sudeste")),
                new StateResponse(15L, "PA", "Pará", new StateResponse.RegionDto(1L, "N", "Norte")),
                new StateResponse(25L, "PB", "Paraíba", new StateResponse.RegionDto(2L, "NE", "Nordeste")),
                new StateResponse(41L, "PR", "Paraná", new StateResponse.RegionDto(4L, "S", "Sul")),
                new StateResponse(26L, "PE", "Pernambuco", new StateResponse.RegionDto(2L, "NE", "Nordeste")),
                new StateResponse(22L, "PI", "Piauí", new StateResponse.RegionDto(2L, "NE", "Nordeste")),
                new StateResponse(33L, "RJ", "Rio de Janeiro", new StateResponse.RegionDto(3L, "SE", "Sudeste")),
                new StateResponse(24L, "RN", "Rio Grande do Norte", new StateResponse.RegionDto(2L, "NE", "Nordeste")),
                new StateResponse(43L, "RS", "Rio Grande do Sul", new StateResponse.RegionDto(4L, "S", "Sul")),
                new StateResponse(11L, "RO", "Rondônia", new StateResponse.RegionDto(1L, "N", "Norte")),
                new StateResponse(14L, "RR", "Roraima", new StateResponse.RegionDto(1L, "N", "Norte")),
                new StateResponse(42L, "SC", "Santa Catarina", new StateResponse.RegionDto(4L, "S", "Sul")),
                new StateResponse(35L, "SP", "São Paulo", new StateResponse.RegionDto(3L, "SE", "Sudeste")),
                new StateResponse(28L, "SE", "Sergipe", new StateResponse.RegionDto(2L, "NE", "Nordeste")),
                new StateResponse(17L, "TO", "Tocantins", new StateResponse.RegionDto(1L, "N", "Norte"))
        ).stream().sorted(Comparator.comparing(StateResponse::nome)).toList();
    }
}
