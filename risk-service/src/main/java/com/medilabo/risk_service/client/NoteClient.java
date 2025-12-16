package com.medilabo.risk_service.client;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.medilabo.risk_service.config.MedilaboApiProperties;
import com.medilabo.risk_service.config.SecurityApiProperties;
import com.medilabo.risk_service.dto.NoteResponseDTO;
import com.medilabo.risk_service.exception.NoteServiceUnavailableException;

import lombok.extern.log4j.Log4j2;

/**
 * HTTP client used by the WebApp to communicate with the Note microservice.
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Configure a {@code RestClient} instance with a base URL and HTTP Basic authentication headers.</li>
 *   <li>Call the Note API to retrieve notes for a given patient.</li>
 *   <li>Convert JSON responses into {@code NoteResponseDTO} objects.</li>
 * </ul>
 *
 */
@Component
@Log4j2
public class NoteClient {
    /**
     * RestClient is synchronous: the call will block until a response is received.
     */    
    private final RestClient rest;

    /**
     * Constructs a {@code NoteClient} instance and configures the {@link RestClient} with a base URL and HTTP Basic authentication.
     *
     * @param builder   the RestClient builder provided by Spring
     * @param baseUrl   the base URL of the Note microservice
     * @param userName  Basic Auth username
     * @param password  Basic Auth password
     */    
    // ce constructeur est appelé gràce à l'annotation @Component : il est utilisé par Spring.
    public NoteClient(RestClient.Builder builder,
            MedilaboApiProperties apiProps,
            SecurityApiProperties securityProps) {

        // encodage pour mettre userName et password dans le header : HttpHeaders.AUTHORIZATION impose cela.
        String basic = Base64.getEncoder()
                .encodeToString((securityProps.getUsername() + ":" + securityProps.getPassword()).getBytes(StandardCharsets.UTF_8));

        this.rest = builder
                .baseUrl(apiProps.getNoteUrlApi())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + basic)
                .build();
    }
    

    /**
     * Retrieves the list of notes associated with a specific patient.
     *
     * <p>
     * The call is synchronous and will block until the Note service responds.
     * The response body is deserialized into a typed {@code List<NoteResponseDTO>}.
     * In case the service returns {@code null}, an empty list is returned instead.
     * </p>
     *
     * @param patientId the ID of the patient whose notes should be retrieved
     * @return a list of {@code NoteResponseDTO}; never {@code null}
     * @throws NoteServiceUnavailableException if the Note microservice is unavailable or unreachable
     */    
    public List<NoteResponseDTO> findByPatientId(Long patientId) {
        try {
            log.debug("NoteClient.findByPatientId");

            List<NoteResponseDTO> notes = Optional.ofNullable(
                    rest.get()
                        .uri("/notes/patient/{patientId}", patientId)
                        .retrieve()
                        // Utilisation de ParameterizedTypeReference pour permettre à RestClient de désérialiser correctement une liste typée (List<NoteResponseDTO>).
                        // Sans cela, il y avait un warning car body(List.class) retournait une List<LinkedHashMap> qu’il aurait fallu ensuite convertir manuellement.
//                        .body(List.class);
                        .body(new ParameterizedTypeReference<List<NoteResponseDTO>>() {})
            ).orElseGet(Collections::emptyList);
            
            log.debug("NoteClient.findByPatientId(id={}) -> notes size: {} {}", patientId, notes.size(), notes);
            return notes;
        // Note a répondu 4xx/5xx
        } catch (org.springframework.web.client.RestClientResponseException e) {
            if (e.getStatusCode().is5xxServerError()) {
                log.error("NoteClient.findByPatientId(id={}) -> Note service 5xx", patientId, e);                
                throw new NoteServiceUnavailableException("Note service 5xx", e);
            }
            throw e; // 4xx propagés tels quels (ex: 400 requête invalide, 404 notes introuvables/mauvaise route)

        } catch (org.springframework.web.client.ResourceAccessException e) {
            // patient injoignable (ex: service non lancé)
            log.error("NoteClient.findByPatientId(id={}) -> Note service unreachable", patientId, e);                
            throw new NoteServiceUnavailableException("Note service unreachable", e);
        }
    }
}
