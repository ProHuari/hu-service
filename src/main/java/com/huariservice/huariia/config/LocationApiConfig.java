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
 * Configuration for IBGE and ViaCEP REST clients for regional citizenship lookup.
 */
@Configuration
public class LocationApiConfig {

    @Bean(name = "ibgeRestClient")
    public RestClient ibgeRestClient(
            @Value("${app.ibge.base-url:https://servicodados.ibge.gov.br/api/v1/localidades}") String baseUrl,
            @Value("${app.ibge.connect-timeout-seconds:5}") int connectTimeout,
            @Value("${app.ibge.read-timeout-seconds:15}") int readTimeout) {

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(createRequestFactory(connectTimeout, readTimeout))
                .defaultHeader("Accept", "application/json")
                .defaultHeader("User-Agent", "Huari-Civic-Platform/1.0")
                .build();
    }

    @Bean(name = "viaCepRestClient")
    public RestClient viaCepRestClient(
            @Value("${app.viacep.base-url:https://viacep.com.br/ws}") String baseUrl,
            @Value("${app.viacep.connect-timeout-seconds:5}") int connectTimeout,
            @Value("${app.viacep.read-timeout-seconds:10}") int readTimeout) {

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(createRequestFactory(connectTimeout, readTimeout))
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
