package com.medilabo.risk_service.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.medilabo.risk_service.dto.NoteResponseDTO;
import com.medilabo.risk_service.exception.NoteServiceUnavailableException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
//Coupler @RequiredArgsConstructor avec des champs final pour rendre les dépendances immuables ==> mieux que @Autowired devenu obsolète.
@RequiredArgsConstructor
@Log4j2
public class NoteClient {
    // RestClient est synchrone : l’appel bloquera jusqu’à la réponse.
    private final RestClient rest;
    @Value("${medilabo.note.url.api}") private String base;

    public List<NoteResponseDTO> findByPatientId(Long patientId) {
        try {
            log.debug("NoteClient.findByPatientId");

            List<NoteResponseDTO> notes = rest
                    .get()
                    .uri(base + "/notes/patient/{patientId}", patientId)
                    .retrieve()
                    // Utilisation de ParameterizedTypeReference pour permettre à RestClient de désérialiser correctement une liste typée (List<NoteResponseDTO>).
                    // Sans cela, il y avait un warning car body(List.class) retournait une List<LinkedHashMap> qu’il aurait fallu ensuite convertir manuellement.
//                    .body(List.class);
                    .body(new ParameterizedTypeReference<List<NoteResponseDTO>>() {});
            if (notes.size() > 0) {
                log.debug("NoteClient.findByPatientId(id={}) -> notes found: {}", patientId, notes);
            } else {
                log.debug("NoteClient.findByPatientId(id={}) -> no notes found", patientId);
            }
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
