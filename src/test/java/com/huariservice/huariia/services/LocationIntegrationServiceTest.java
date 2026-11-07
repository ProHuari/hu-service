package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.location.CepLookupResponse;
import com.huariservice.huariia.dto.location.MunicipalityResponse;
import com.huariservice.huariia.dto.location.StateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationIntegrationServiceTest {

    @Mock
    private RestClient ibgeRestClient;

    @Mock
    private RestClient viaCepRestClient;

    @Mock
    private RestClient.RequestHeadersUriSpec ibgeUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec ibgeHeadersSpec;

    @Mock
    private RestClient.ResponseSpec ibgeResponseSpec;

    @Mock
    private RestClient.RequestHeadersUriSpec viaCepUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec viaCepHeadersSpec;

    @Mock
    private RestClient.ResponseSpec viaCepResponseSpec;

    private LocationIntegrationService service;

    @BeforeEach
    void setUp() {
        service = new LocationIntegrationService(
                ibgeRestClient,
                viaCepRestClient,
                "https://servicodados.ibge.gov.br/api/v1/localidades",
                "https://viacep.com.br/ws"
        );
    }

    @Test
    @DisplayName("Should fetch states list from IBGE successfully")
    void shouldFetchStatesSuccessfully() {
        StateResponse sp = new StateResponse(35L, "SP", "São Paulo", new StateResponse.RegionDto(3L, "SE", "Sudeste"));
        StateResponse rj = new StateResponse(33L, "RJ", "Rio de Janeiro", new StateResponse.RegionDto(3L, "SE", "Sudeste"));

        when(ibgeRestClient.get()).thenReturn(ibgeUriSpec);
        when(ibgeUriSpec.uri("/estados?orderBy=nome")).thenReturn(ibgeHeadersSpec);
        when(ibgeHeadersSpec.retrieve()).thenReturn(ibgeResponseSpec);
        when(ibgeResponseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(List.of(rj, sp));

        List<StateResponse> states = service.getStates();

        assertNotNull(states);
        assertEquals(2, states.size());
        assertEquals("RJ", states.get(0).sigla());
        assertEquals("SP", states.get(1).sigla());
    }

    @Test
    @DisplayName("Should return resilient fallback states list when IBGE API fails")
    void shouldReturnFallbackStatesWhenIbgeFails() {
        when(ibgeRestClient.get()).thenReturn(ibgeUriSpec);
        when(ibgeUriSpec.uri(anyString())).thenThrow(new ResourceAccessException("IBGE service unavailable"));

        List<StateResponse> states = service.getStates();

        assertNotNull(states);
        assertEquals(27, states.size()); // all 27 Brazilian UFs present in contingency list
        assertTrue(states.stream().anyMatch(s -> s.sigla().equals("SP")));
        assertTrue(states.stream().anyMatch(s -> s.sigla().equals("DF")));
    }

    @Test
    @DisplayName("Should fetch municipalities for a valid state UF")
    void shouldFetchMunicipalitiesSuccessfully() {
        MunicipalityResponse mun1 = new MunicipalityResponse(3550308L, "São Paulo");
        MunicipalityResponse mun2 = new MunicipalityResponse(3509502L, "Campinas");

        when(ibgeRestClient.get()).thenReturn(ibgeUriSpec);
        when(ibgeUriSpec.uri("/estados/{uf}/municipios", "SP")).thenReturn(ibgeHeadersSpec);
        when(ibgeHeadersSpec.retrieve()).thenReturn(ibgeResponseSpec);
        when(ibgeResponseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(List.of(mun1, mun2));

        List<MunicipalityResponse> municipalities = service.getMunicipalities("sp");

        assertNotNull(municipalities);
        assertEquals(2, municipalities.size());
        assertEquals("São Paulo", municipalities.get(0).nome());
    }

    @Test
    @DisplayName("Should return empty list for invalid UF code without remote call")
    void shouldReturnEmptyListForInvalidUf() {
        List<MunicipalityResponse> result = service.getMunicipalities("INVALID_UF");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should look up postal code (CEP) from ViaCEP successfully and clean digits")
    void shouldLookupCepSuccessfully() {
        CepLookupResponse viaCepData = new CepLookupResponse(
                "01310-100",
                "Avenida Paulista",
                "de 611 a 1045 - lado ímpar",
                "Bela Vista",
                "São Paulo",
                "SP",
                "São Paulo",
                "Sudeste",
                "3550308",
                "11",
                false
        );

        when(viaCepRestClient.get()).thenReturn(viaCepUriSpec);
        when(viaCepUriSpec.uri("/{cep}/json", "01310100")).thenReturn(viaCepHeadersSpec);
        when(viaCepHeadersSpec.retrieve()).thenReturn(viaCepResponseSpec);
        when(viaCepResponseSpec.body(CepLookupResponse.class)).thenReturn(viaCepData);

        CepLookupResponse result = service.lookupCep("01310-100");

        assertNotNull(result);
        assertEquals("SP", result.uf());
        assertEquals("São Paulo", result.localidade());
        assertFalse(result.hasError());
    }

    @Test
    @DisplayName("Should return fallback response when ViaCEP fails with timeout or error")
    void shouldReturnFallbackWhenViaCepFails() {
        when(viaCepRestClient.get()).thenReturn(viaCepUriSpec);
        when(viaCepUriSpec.uri(anyString(), anyString())).thenThrow(new ResourceAccessException("ViaCEP timeout"));

        CepLookupResponse result = service.lookupCep("70160-900");

        assertNotNull(result);
        assertTrue(result.hasError());
        assertEquals("BR", result.uf());
    }
}

