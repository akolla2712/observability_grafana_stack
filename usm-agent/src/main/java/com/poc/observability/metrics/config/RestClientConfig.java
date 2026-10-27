package com.poc.observability.metrics.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${asset.search.url:http://localhost:8082/search-asset}")
    private String assetSearchUrl;

    @Value("${asset.create.url:http://localhost:8082/create-asset}")
    private String assetCreateUrl;

    @Bean
    public RestClient assetRestClient() {
        return RestClient.builder()
                .requestFactory(clientHttpRequestFactory())
                .baseUrl(assetSearchUrl)
                .build();
    }

    @Bean
    public RestClient assetCreateRestClient() {
        return RestClient.builder()
                .requestFactory(clientHttpRequestFactory())
                .baseUrl(assetCreateUrl)
                .build();
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        return factory;
    }
}
