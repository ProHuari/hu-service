package com.huariservice.huariia.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Public GET on /cursos should be permitted without authentication")
    void withoutToken_courses_isPermitted() throws Exception {
        mockMvc.perform(get("/cursos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Public GET on /transparencia/parlamentares should be permitted without authentication")
    void withoutToken_transparency_isPermitted() throws Exception {
        mockMvc.perform(get("/transparencia/parlamentares"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Public GET on /transparencia/tse/candidatos should be permitted without authentication")
    void withoutToken_tseCandidates_isPermitted() throws Exception {
        mockMvc.perform(get("/transparencia/tse/candidatos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Public GET on /transparencia/empresas/{cnpj} should be permitted without authentication")
    void withoutToken_companyCnpj_isPermitted() throws Exception {
        mockMvc.perform(get("/transparencia/empresas/00000000000191"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET on /aulas must require authentication")
    void withoutToken_lessons_returns401() throws Exception {
        mockMvc.perform(get("/aulas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST on /auth/logout should be permitted to gracefully handle client logouts")
    void withoutToken_logout_returns204() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST on /cursos must require authentication")
    void withoutToken_postCourse_returns401() throws Exception {
        mockMvc.perform(post("/cursos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("CORS preflight OPTIONS /cursos should allow http://localhost:5173")
    void corsPreflight_localhost5173_isAllowed() throws Exception {
        mockMvc.perform(options("/cursos")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    @DisplayName("POST on /transparencia/camara/sincronizacoes must require authentication and return standard ErrorResponse")
    void withoutToken_camaraSync_returns401WithStandardErrorBody() throws Exception {
        mockMvc.perform(post("/transparencia/camara/sincronizacoes"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Public GET on /transparencia/despesas/resumo should return 200 with aggregated summary fields")
    void withoutToken_expenseSummary_returns200() throws Exception {
        mockMvc.perform(get("/transparencia/despesas/resumo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").exists())
                .andExpect(jsonPath("$.totalExpenses").value(0))
                .andExpect(jsonPath("$.rankingByParliamentarian").isArray())
                .andExpect(jsonPath("$.distributionByCategory").isArray())
                .andExpect(jsonPath("$.distributionByState").isArray());
    }

    @Test
    @DisplayName("Public GET on /transparencia/despesas/{id} should return 404 for unknown expense")
    void withoutToken_expenseById_unknownReturns404() throws Exception {
        mockMvc.perform(get("/transparencia/despesas/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}

