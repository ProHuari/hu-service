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
 * Configuration for the external Tribunal de Contas (TCU / TCE) REST client.
 */
@Configuration
public class AuditCourtConfig {

    @Bean(name = "auditCourtRestClient")
    public RestClient auditCourtRestClient(
            @Value("${app.audit-court.base-url:https://dadosabertos.tcu.gov.br/api}") String baseUrl,
            @Value("${app.audit-court.connect-timeout-seconds:5}") int connectTimeout,
            @Value("${app.audit-court.read-timeout-seconds:15}") int readTimeout) {

        ClientHttpRequestFactory factory = createRequestFactory(connectTimeout, readTimeout);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .defaultHeader("Accept", "application/json")
                .defaultHeader("User-Agent", "Huari-Civic-Platform/1.0")
                .build();
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
