package com.medilabo.webapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

// permet de configurer RestClient.
@Configuration
public class HttpClientsConfig {
    @Bean
    RestClient restClient(@Value("${medilabo.url.api}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl) 
                .build();
    }
}
