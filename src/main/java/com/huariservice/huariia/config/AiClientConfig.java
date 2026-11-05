package com.huariservice.huariia.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.io.UncheckedIOException;
import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@ConditionalOnProperty(name = "app.ia.enabled", havingValue = "true")
public class AiClientConfig {

    @Bean(name = {"aiRestClient", "iaRestClient"})
    public RestClient aiRestClient(
            @Value("${app.ia.base-url}") String baseUrl,
            @Value("${app.ia.api-key}") String apiKey) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("app.ia.enabled=true but IA_API_KEY was not configured.");
        }

        ClientHttpRequestFactory factory = createRequestFactory();

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .defaultHeader("x-goog-api-key", apiKey)
                .defaultHeader("content-type", "application/json")
                .build();
    }

    private ClientHttpRequestFactory createRequestFactory() {
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();
            JdkClientHttpRequestFactory jdkFactory = new JdkClientHttpRequestFactory(httpClient);
            jdkFactory.setReadTimeout(Duration.ofSeconds(30));
            return jdkFactory;
        } catch (UncheckedIOException ex) {
            SimpleClientHttpRequestFactory simpleFactory = new SimpleClientHttpRequestFactory();
            simpleFactory.setConnectTimeout(Duration.ofSeconds(5));
            simpleFactory.setReadTimeout(Duration.ofSeconds(30));
            return simpleFactory;
        }
    }
}

