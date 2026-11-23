package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.tse.TseAssetDto;
import com.huariservice.huariia.dto.tse.TseCandidateAssetsResponse;
import com.huariservice.huariia.dto.tse.TseCandidateSummaryDto;
import com.huariservice.huariia.services.TseIntegrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TseIntegrationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TseIntegrationService tseService;

    @InjectMocks
    private TseIntegrationController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /transparencia/tse/candidatos returns 200 with candidates list")
    void shouldReturnCandidatesList() throws Exception {
        List<TseCandidateSummaryDto> candidates = List.of(
                new TseCandidateSummaryDto(
                        250001600123L, "Tabata Amaral", "Tabata Claudia Amaral de Pontes", 4000, "PSB",
                        "Deputado Federal", "SP", 2022, new BigDecimal("807446.50"), "DEFERIDO", null
                )
        );

        when(tseService.fetchCandidates(any(), any(), anyInt(), any())).thenReturn(candidates);

        mockMvc.perform(get("/transparencia/tse/candidatos")
                        .param("nome", "Tabata")
                        .param("uf", "SP")
                        .param("ano", "2022")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(250001600123L))
                .andExpect(jsonPath("$[0].nomeUrna").value("Tabata Amaral"))
                .andExpect(jsonPath("$[0].siglaPartido").value("PSB"));
    }

    @Test
    @DisplayName("GET /transparencia/tse/candidatos/{id}/bens returns 200 with candidate assets")
    void shouldReturnCandidateAssets() throws Exception {
        TseCandidateAssetsResponse response = TseCandidateAssetsResponse.ofOnline(
                250001600123L,
                "Tabata Amaral",
                "Tabata Claudia Amaral de Pontes",
                "PSB",
                "Deputado Federal",
                "SP",
                2022,
                new BigDecimal("807446.50"),
                List.of(
                        new TseAssetDto(1, "Apartamento residencial", "Imóvel residencial", new BigDecimal("600000.00")),
                        new TseAssetDto(2, "Aplicação renda fixa", "Investimento", new BigDecimal("207446.50"))
                )
        );

        when(tseService.fetchCandidateAssets(anyLong(), anyInt(), anyString())).thenReturn(response);

        mockMvc.perform(get("/transparencia/tse/candidatos/{id}/bens", 250001600123L)
                        .param("ano", "2022")
                        .param("uf", "SP")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.candidatoId").value(250001600123L))
                .andExpect(jsonPath("$.nomeUrna").value("Tabata Amaral"))
                .andExpect(jsonPath("$.status").value("ONLINE"))
                .andExpect(jsonPath("$.quantidadeBens").value(2))
                .andExpect(jsonPath("$.bens[0].descricao").value("Apartamento residencial"));
    }
}
