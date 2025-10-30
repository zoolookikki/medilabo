package com.medilabo.webapp.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

import lombok.extern.log4j.Log4j2;

// permet de configurer RestClient.
@Configuration
@Log4j2
public class HttpClientsConfig {
    @Bean
    RestClient restClient(
            @Value("${medilabo.url.api}") String baseUrl,
            @Value("${security.api.username}") String userName,
            @Value("${security.api.password}") String password) {
        
        log.debug("HttpClientsConfig/restClient,baseUrl="+baseUrl+",userName="+userName+",password="+password);
        // encodage pour mettre userName et password dans le header : HttpHeaders.AUTHORIZATION impose cela.
        String basic = Base64.getEncoder()
                .encodeToString((userName + ":" + password).getBytes(StandardCharsets.UTF_8));
        
        return RestClient.builder()
                .baseUrl(baseUrl) 
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + basic)
                .build();
    }
}
