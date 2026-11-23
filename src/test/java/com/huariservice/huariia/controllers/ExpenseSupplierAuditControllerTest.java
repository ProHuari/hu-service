package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.ParliamentarianExpenseResponse;
import com.huariservice.huariia.dto.cnpj.CnpjCompanyResponse;
import com.huariservice.huariia.dto.cnpj.ExpenseSupplierAuditResponse;
import com.huariservice.huariia.exceptions.GlobalExceptionHandler;
import com.huariservice.huariia.services.CnpjIntegrationService;
import com.huariservice.huariia.services.PoliticalTransparencyService;
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
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ExpenseSupplierAuditControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PoliticalTransparencyService transparencyService;

    @Mock
    private CnpjIntegrationService cnpjIntegrationService;

    @InjectMocks
    private PoliticalTransparencyController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /transparencia/despesas/{id}/fornecedor returns 200 with combined supplier audit and risk analysis")
    void shouldReturnExpenseSupplierAudit() throws Exception {
        ParliamentarianExpenseResponse expense = new ParliamentarianExpenseResponse(
                1L, 10L, "Tabata Amaral", "Divulgação da Atividade Parlamentar",
                "Gráfica & Comunicação Digital Brasil (02.012.862/0001-60)",
                new BigDecimal("4500.00"), LocalDate.now(), "https://camara.leg.br/doc.pdf"
        );

        CnpjCompanyResponse company = CnpjCompanyResponse.ofOnline(
                "02012862000160",
                "02.012.862/0001-60",
                "GRAFICA BRASIL LTDA",
                "GRAFICA BRASIL",
                "ATIVA",
                "2018-05-10",
                "2018-05-10",
                1813001,
                "Impressão de material para uso publicitário",
                new BigDecimal("150000.00"),
                "ME",
                "RUA DAS ARTES",
                "100",
                "SAO PAULO",
                "SP",
                "01001000",
                Collections.emptyList(),
                Collections.emptyList()
        );

        ExpenseSupplierAuditResponse audit = ExpenseSupplierAuditResponse.of(
                expense,
                company,
                List.of(),
                "BAIXO",
                "Auditoria concluída com sucesso. Nenhuma anomalia cadastral detectada."
        );

        when(transparencyService.findExpenseById(1L)).thenReturn(expense);
        when(cnpjIntegrationService.auditExpenseSupplier(any(), any())).thenReturn(audit);

        mockMvc.perform(get("/transparencia/despesas/1/fornecedor")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.despesa.id").value(1L))
                .andExpect(jsonPath("$.fornecedor.cnpj").value("02012862000160"))
                .andExpect(jsonPath("$.nivelRisco").value("BAIXO"))
                .andExpect(jsonPath("$.mensagem").value("Auditoria concluída com sucesso. Nenhuma anomalia cadastral detectada."));
    }
}
