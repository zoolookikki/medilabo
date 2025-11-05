package com.medilabo.webapp.client;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.medilabo.webapp.dto.NoteRequestDTO;
import com.medilabo.webapp.dto.NoteResponseDTO;
import com.medilabo.webapp.exception.NoteServiceUnavailableException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
//Coupler @RequiredArgsConstructor avec des champs final pour rendre les dépendances immuables ==> mieux que @Autowired devenu obsolète.
@RequiredArgsConstructor
@Log4j2
public class NoteClient {
    /*
    RestClient est synchrone : l’appel bloquera jusqu’à la réponse.
    Grâce au build, rest devient immutable donc ne peut plus être modifié.
    Il faut donc supprimer l'alerte Spotbugs car il ne le voit pas.
    */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "Spring RestClient is immutable and thread-safe; no internal state exposure."
    )
    private final RestClient rest;

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

            log.debug("NoteClient.findByPatientId(id={}) -> notes size: {}", patientId, notes.size());
            return notes;
        } catch (RestClientResponseException e) {
            log.error("NoteClient.findByPatientId(id={}) -> Note service unavailable", patientId, e);
            throw new NoteServiceUnavailableException("Note service unavailable", e);
        }
    }

    public NoteResponseDTO create(NoteRequestDTO noteRequestDTO) {
        try {
            log.debug("NoteClient.create");
            NoteResponseDTO created = rest
                    .post()
                    .uri("/notes")
                    .body(noteRequestDTO)
                    .retrieve()
                    .body(NoteResponseDTO.class);
            log.debug("NoteClient.create() -> created note: {}", created);
            return created;
        } catch (RestClientResponseException e) {
            log.error("NoteClient.create() -> Note service unavailable", e);
            throw new NoteServiceUnavailableException("Note service unavailable", e);
        }
    }    
}
