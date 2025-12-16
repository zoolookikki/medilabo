package com.medilabo.webapp.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

import lombok.extern.log4j.Log4j2;

/**
 * Spring configuration class responsible for creating and configuring the shared {@link RestClient} used by the WebApp to communicate with upstream microservices (Patient, Note, Risk).
 *
 * @see RestClient
 * @see org.springframework.context.annotation.Configuration
 */
// permet de configurer RestClient.
@Configuration
@Log4j2
public class HttpClientsConfig {
    /**
     * Creates and configures the shared {@link RestClient} instance used by all WebApp clients.
     *
     * @param baseUrl   the base URL for all outgoing HTTP requests
     * @param userName  the username used for HTTP Basic authentication
     * @param password  the password used for HTTP Basic authentication
     * @return a fully configured, immutable {@link RestClient} instance
     */
    @Bean
    RestClient restClient(
            MedilaboApiProperties apiProps,
            SecurityApiProperties securityProps) {
        
        log.debug("HttpClientsConfig/restClient,baseUrl="+apiProps.getUrlApi()+",userName="+securityProps.getUsername()+",password="+securityProps.getPassword());
        // encodage pour mettre userName et password dans le header : HttpHeaders.AUTHORIZATION impose cela.
        String basic = Base64.getEncoder()
                .encodeToString((securityProps.getUsername() + ":" + securityProps.getPassword()).getBytes(StandardCharsets.UTF_8));
        
        return RestClient.builder()
                .baseUrl(apiProps.getUrlApi()) 
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + basic)
                .build();
    }
}
