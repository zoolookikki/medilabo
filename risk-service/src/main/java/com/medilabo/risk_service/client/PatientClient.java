package com.medilabo.risk_service.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.medilabo.risk_service.dto.PatientResponseDTO;
import com.medilabo.risk_service.exception.PatientServiceUnavailableException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Value;

@Component
//Coupler @RequiredArgsConstructor avec des champs final pour rendre les dépendances immuables ==> mieux que @Autowired devenu obsolète.
@RequiredArgsConstructor
@Log4j2
public class PatientClient {
    // RestClient est synchrone : l’appel bloquera jusqu’à la réponse.
    private final RestClient rest;
    @Value("${medilabo.patient.url.api}") private String base;

    public PatientResponseDTO findById(Long id) {
        try {
            log.debug("PatientClient.findById");

            PatientResponseDTO patient = rest
                .get()
                .uri(base + "/patients/{id}", id)
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
