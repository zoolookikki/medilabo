package com.medilabo.risk_service.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.medilabo.risk_service.dto.PatientResponseDTO;
import com.medilabo.risk_service.exception.PatientServiceUnavailableException;

import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

/**
 * HTTP client used by the WebApp to communicate with the Patient microservice.
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Configure a {@code RestClient} instance with the appropriate base URL and HTTP Basic authentication header.</li>
 *   <li>Send requests to the Patient microservice to fetch patient data.</li>
 *   <li>Convert JSON responses into {@code PatientResponseDTO} objects.</li>
 * </ul>
 */
@Component
@Log4j2
public class PatientClient {
    /**
     * RestClient is synchronous: the call will block until a response is received.
     */    
    private final RestClient rest;

    /**
     * Constructs a {@code PatientClient} instance and configures the {@link RestClient} with a base URL and HTTP Basic authentication.
     *
     * @param builder   the RestClient builder provided by Spring
     * @param baseUrl   the base URL of the Patient microservice
     * @param userName  Basic Auth username
     * @param password  Basic Auth password
     */    
    // ce constructeur est appelé gràce à l'annotation @Component : il est utilisé par Spring.
    // le bean RestClient.Builder est fourni par Spring Boot.   
    public PatientClient(RestClient.Builder builder,
            @Value("${medilabo.patient.url.api}") String baseUrl,
            @Value("${security.api.username}") String userName,
            @Value("${security.api.password}") String password) {

        // encodage pour mettre userName et password dans le header : HttpHeaders.AUTHORIZATION impose cela.
        String basic = Base64.getEncoder()
                .encodeToString((userName + ":" + password).getBytes(StandardCharsets.UTF_8));

        this.rest = builder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + basic)
                .build();
    }
    

    /**
     * Retrieves a patient by its identifier by calling the Patient microservice.
     *
     * <p>
     * The request is synchronous and will block until the service responds.
     * If the service returns {@code null}, the method returns {@code null}
     * </p>
     *
     * @param id the patient identifier
     * @return a {@code PatientResponseDTO} instance, or {@code null} if no patient exists
     * @throws PatientServiceUnavailableException if the Patient service is down or unreachable
     */    
    public PatientResponseDTO findById(Long id) {
        try {
            log.debug("PatientClient.findById");

            PatientResponseDTO patient = rest
                .get()
                .uri("/patients/{id}", id)
                .retrieve()
                .body(PatientResponseDTO.class);

            if (patient != null) {
                log.debug("PatientClient.findById(id={}) -> patient found: {}", id, patient);
            } else {
                log.debug("PatientClient.findById(id={}) -> no patient found", id);
            }

            return patient;
        // Patient a répondu 4xx/5xx
        } catch (org.springframework.web.client.RestClientResponseException e) {
            if (e.getStatusCode().is5xxServerError()) {
                log.error("PatientClient.findById(id={}) -> Patient service 5xx", id, e);                
                throw new PatientServiceUnavailableException("Patient service 5xx", e);
            }
            throw e; // 4xx propagés tels quels (ex: 400 requête invalide, 404 patient introuvable/mauvaise route)

        } catch (org.springframework.web.client.ResourceAccessException e) {
            // patient injoignable (ex: service non lancé)
            log.error("PatientClient.findById(id={}) -> Patient service unreachable", id, e);                
            throw new PatientServiceUnavailableException("Patient service unreachable", e);
        }
    }
}
