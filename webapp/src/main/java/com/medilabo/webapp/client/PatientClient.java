package com.medilabo.webapp.client;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.medilabo.webapp.dto.PatientRequestDTO;
import com.medilabo.webapp.dto.PatientResponseDTO;
import com.medilabo.webapp.exception.PatientServiceUnavailableException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
//Coupler @RequiredArgsConstructor avec des champs final pour rendre les dépendances immuables ==> mieux que @Autowired devenu obsolète.
@RequiredArgsConstructor
@Log4j2
public class PatientClient {
    // RestClient est synchrone : l’appel bloquera jusqu’à la réponse.
    private final RestClient rest;

    public List<PatientResponseDTO> findAll() {
        try {
            log.debug("PatientClient.findAll");

            List<PatientResponseDTO> patients = rest
                // prépare une requête GET vers le microservice Patient
                .get()
                .uri("/patients")
                // exécute la requête
                .retrieve()
                // Jackson désérialise la réponse JSON en List<PatientResponseDTO>
                .body(new ParameterizedTypeReference<List<PatientResponseDTO>>() {});

            log.debug("PatientClient.findAll() -> {} patients found", patients != null ? patients.size() : 0);

            return patients;

        // attention ici, bien faire la distinction sinon on ne sait pas si c'est la gateway ou le microservice Patient qui n'a pas répondu.
        } catch (org.springframework.web.client.RestClientResponseException e) {
            // Si on arrive ici, c'est que la gateway a répondu (sinon ce serait une ConnectException).
            log.error("PatientClient.findAll() -> Patient service unavailable", e);
            throw new PatientServiceUnavailableException("Patient service unavailable", e);
        }
    }

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

        } catch (org.springframework.web.client.RestClientResponseException e) {
            log.error("PatientClient.findById(id={}) -> Patient service unavailable", id, e);
            throw new PatientServiceUnavailableException("Patient service unavailable", e);
        }
    }
    
    public PatientResponseDTO create(PatientRequestDTO patientRequestDTO) {
        try {
            log.debug("PatientClient.create");

            PatientResponseDTO created = rest
                // prépare une requête POST vers le microservice Patient
                .post()
                .uri("/patients")
                // Jackson sérialise patientRequestDTO en JSON (request body)
                .body(patientRequestDTO)
                // exécute la requête
                .retrieve()
                // Jackson désérialise la réponse JSON en PatientResponseDTO (response body)
                .body(PatientResponseDTO.class);

            log.debug("PatientClient.create() -> created patient: {}", created);

            return created;

        } catch (org.springframework.web.client.RestClientResponseException e) {
            log.error("PatientClient.create() -> Patient service unavailable", e);
            throw new PatientServiceUnavailableException("Patient service unavailable", e);
        }
    }
    
    public PatientResponseDTO update(Long id, PatientRequestDTO patientRequestDTO) {
        try {
            log.debug("PatientClient.update");

            PatientResponseDTO updated = rest
                // prépare une requête PUT vers le microservice Patient
                .put()
                .uri("/patients/{id}", id)
                // Jackson sérialise patientRequestDTO en JSON (request body)
                .body(patientRequestDTO)
                // exécute la requête
                .retrieve()
                // Jackson désérialise la réponse JSON en PatientResponseDTO (response body)
                .body(PatientResponseDTO.class);

            log.debug("PatientClient.update(id={}) -> updated patient: {}", id, updated);

            return updated;

        } catch (org.springframework.web.client.RestClientResponseException e) {
            log.error("PatientClient.update(id={}) -> Patient service unavailable", id, e);
            throw new PatientServiceUnavailableException("Patient service unavailable", e);
        }
    }
}
