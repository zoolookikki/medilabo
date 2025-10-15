package com.medilabo.risk_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

// permet de configurer RestClient.
@Configuration
public class HttpClientsConfig {
    @Bean
    RestClient restClient(RestClient.Builder builder) {
        return builder.build(); 
    }
}
