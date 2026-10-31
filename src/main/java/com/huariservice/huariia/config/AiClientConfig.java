package com.huariservice.huariia.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * HTTP client configuration for the AI provider. Active only when
 * {@code app.ia.enabled=true}.
 */
@Configuration
@ConditionalOnProperty(name = "app.ia.enabled", havingValue = "true")
public class AiClientConfig {

    /**
     * Builds the REST client used to call the AI provider, with a 5s connect
     * timeout
     * and a 30s read timeout.
     *
     * @param baseUrl provider base URL, from {@code app.ia.base-url}
     * @param apiKey  provider API key, from {@code app.ia.api-key}
     * @return the configured REST client
     * @throws IllegalStateException if the API key is missing
     */
    @Bean
    public RestClient aiRestClient(
            @Value("${app.ia.base-url}") String baseUrl,
            @Value("${app.ia.api-key}") String apiKey) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("app.ia.enabled=true but IA_API_KEY is not set.");
        }

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(30));

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .defaultHeader("x-goog-api-key", apiKey)
                .defaultHeader("content-type", "application/json")
                .build();
    }
}
