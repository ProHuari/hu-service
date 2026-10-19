package com.huariservice.huariia.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@ConditionalOnProperty(name = "app.ia.enabled", havingValue = "true")
public class IaClientConfig {

    @Bean
    public RestClient iaRestClient(
            @Value("${app.ia.base-url}") String baseUrl,
            @Value("${app.ia.api-key}") String apiKey) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("app.ia.enabled=true mas IA_API_KEY não foi definida.");
        }

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(30));

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("content-type", "application/json")
                .build();
    }
}