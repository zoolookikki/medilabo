package com.medilabo.risk_service.client;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.medilabo.risk_service.dto.NoteResponseDTO;
import com.medilabo.risk_service.exception.NoteServiceUnavailableException;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class NoteClient {
    // RestClient est synchrone : l’appel bloquera jusqu’à la réponse.
    private final RestClient rest;
    
    // ce constructeur est appelé gràce à l'annotation @Component : il est utilisé par Spring.
    // le bean RestClient.Builder est fourni par Spring Boot.   
    public NoteClient(RestClient.Builder builder,
            @Value("${medilabo.note.url.api}") String baseUrl,
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
