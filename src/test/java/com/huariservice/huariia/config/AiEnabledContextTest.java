package com.huariservice.huariia.config;

import com.huariservice.huariia.services.AiAnswerGenerator;
import com.huariservice.huariia.services.GeminiAiAnswerGenerator;
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
        "app.ia.model=gemini-1.5-flash"
})
class AiEnabledContextTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private AiAnswerGenerator aiAnswerGenerator;

    @Test
    @DisplayName("Ensures RestClient bean and Gemini implementation load when AI is enabled")
    void shouldLoadContextWithAiEnabled() {
        assertThat(context.containsBean("aiRestClient")).isTrue();
        assertThat(context.getBean("aiRestClient", RestClient.class)).isNotNull();
        assertThat(aiAnswerGenerator).isInstanceOf(GeminiAiAnswerGenerator.class);
    }
}
