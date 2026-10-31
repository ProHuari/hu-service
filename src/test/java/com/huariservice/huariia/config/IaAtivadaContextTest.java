package com.huariservice.huariia.config;

import com.huariservice.huariia.services.AiResponseGenerator;
import com.huariservice.huariia.services.GeminiAiResponseGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "app.ia.enabled=true",
        "app.ia.api-key=dummy-test-key",
        "app.ia.model=gemini-2.5-flash"
})
class IaAtivadaContextTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private AiResponseGenerator aiResponseGenerator;

    @Test
    @DisplayName("Garante que o bean RestClient e a implementação Gemini sobem quando a IA está ativada")
    void deveSubirContextoComIaAtivada() {
        assertThat(context.containsBean("iaRestClient")).isTrue();
        assertThat(context.getBean("iaRestClient", RestClient.class)).isNotNull();
        assertThat(aiResponseGenerator).isInstanceOf(GeminiAiResponseGenerator.class);
    }
}
