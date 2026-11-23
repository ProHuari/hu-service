package com.huariservice.huariia.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.io.UncheckedIOException;
import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Unified configuration for the Google Gemini Generative AI REST client (v1beta).
 * Exposes bean aliases ({@code geminiRestClient}, {@code aiRestClient}, {@code iaRestClient})
 * so both the AI history generator and the Citizen AI Tutor share one consistent HTTP client.
 */
@Configuration
public class GeminiApiConfig {

    @Bean(name = {"geminiRestClient", "aiRestClient", "iaRestClient"})
    public RestClient geminiRestClient(
            @Value("${app.ai.gemini.base-url:https://generativelanguage.googleapis.com/v1beta}") String baseUrl,
            @Value("${app.ai.gemini.api-key:${app.ia.api-key:}}") String apiKey,
            @Value("${app.ai.gemini.connect-timeout-seconds:5}") int connectTimeout,
            @Value("${app.ai.gemini.read-timeout-seconds:30}") int readTimeout) {

        ClientHttpRequestFactory factory = createRequestFactory(connectTimeout, readTimeout);

        RestClient.Builder builder = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("User-Agent", "Huari-Civic-Platform/1.0");

        if (apiKey != null && !apiKey.isBlank() && !apiKey.equalsIgnoreCase("sua_chave_aqui")) {
            builder.defaultHeader("x-goog-api-key", apiKey.trim());
        }

        return builder.build();
    }

    private ClientHttpRequestFactory createRequestFactory(int connectTimeout, int readTimeout) {
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(connectTimeout))
                    .build();
            JdkClientHttpRequestFactory jdkFactory = new JdkClientHttpRequestFactory(httpClient);
            jdkFactory.setReadTimeout(Duration.ofSeconds(readTimeout));
            return jdkFactory;
        } catch (UncheckedIOException ex) {
            SimpleClientHttpRequestFactory simpleFactory = new SimpleClientHttpRequestFactory();
            simpleFactory.setConnectTimeout(Duration.ofSeconds(connectTimeout));
            simpleFactory.setReadTimeout(Duration.ofSeconds(readTimeout));
            return simpleFactory;
        }
    }
}
