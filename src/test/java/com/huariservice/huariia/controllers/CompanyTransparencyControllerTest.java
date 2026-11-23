package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.cnpj.CnpjCompanyResponse;
import com.huariservice.huariia.exceptions.GlobalExceptionHandler;
import com.huariservice.huariia.services.CnpjIntegrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CompanyTransparencyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CnpjIntegrationService cnpjService;

    @InjectMocks
    private CompanyTransparencyController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /transparencia/empresas/{cnpj} returns 200 with company registration data")
    void shouldReturnCompanyData() throws Exception {
        CnpjCompanyResponse company = CnpjCompanyResponse.ofOnline(
                "00000000000191",
                "00.000.000/0001-91",
                "BANCO DO BRASIL SA",
                "DIRECAO GERAL",
                "ATIVA",
                "2005-11-03",
                "1966-08-01",
                6422100,
                "Bancos múltiplos",
                new BigDecimal("12000000000.00"),
                "DEMAIS",
                "SAUN",
                "SN",
                "BRASILIA",
                "DF",
                "70073901",
                Collections.emptyList(),
                List.of()
        );

        when(cnpjService.fetchCompanyByCnpj("00000000000191")).thenReturn(company);

        mockMvc.perform(get("/transparencia/empresas/00000000000191")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cnpj").value("00000000000191"))
                .andExpect(jsonPath("$.cnpjFormatado").value("00.000.000/0001-91"))
                .andExpect(jsonPath("$.razaoSocial").value("BANCO DO BRASIL SA"))
                .andExpect(jsonPath("$.situacaoCadastral").value("ATIVA"));
    }

    @Test
    @DisplayName("GET /transparencia/empresas/{cnpj} with invalid digits returns 400 Bad Request")
    void shouldReturn400OnInvalidCnpj() throws Exception {
        when(cnpjService.fetchCompanyByCnpj(anyString()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "O CNPJ deve conter 14 dígitos numéricos válidos."));

        mockMvc.perform(get("/transparencia/empresas/123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("O CNPJ deve conter 14 dígitos numéricos válidos."));
    }
}
