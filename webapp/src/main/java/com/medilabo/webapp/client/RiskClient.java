package com.medilabo.webapp.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.medilabo.webapp.dto.RiskResponseDTO;
import com.medilabo.webapp.exception.RiskServiceUnavailableException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.client.RestClientResponseException;

/**
 * Client for communicating with the Risk microservice.
 *
 * <p>This component performs synchronous HTTP requests to obtain the computed medical risk level for a given patient. </p>
 *
 * @see org.springframework.web.client.RestClient
 * @see com.medilabo.webapp.dto.RiskResponseDTO
 * @see com.medilabo.webapp.exception.RiskServiceUnavailableException
 */
@Component
//Coupler @RequiredArgsConstructor avec des champs final pour rendre les dépendances immuables ==> mieux que @Autowired devenu obsolète.
@RequiredArgsConstructor
@Log4j2
public class RiskClient {
    /**
     * RestClient is synchronous: the call will block until a response is received.
     */
    /*
    Grâce au build, rest devient immutable donc ne peut plus être modifié.
    Il faut donc supprimer l'alerte Spotbugs car il ne le voit pas.
    */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "Spring RestClient is immutable and thread-safe; no internal state exposure."
    )
    private final RestClient rest;

    /**
     * Retrieves the computed medical risk level for a specific patient.
     *
     * @param patientId the unique identifier of the patient whose risk is requested
     * @return a {@link RiskResponseDTO} containing the computed risk level
     * @throws com.medilabo.webapp.exception.RiskServiceUnavailableException
     *         if the Risk microservice is unreachable or returns a 5xx status
     */    
    public RiskResponseDTO getRisk(Long patientId) {
        try {
            log.debug("RiskClient.getRisk(patientId={})", patientId);
            RiskResponseDTO risk = rest
                .get()
                .uri("/risk/{id}", patientId)
                .retrieve()
                .body(RiskResponseDTO.class);

            if (risk != null) {
                log.debug("RiskClient.getRisk(patientId={}) -> risk found: {}", patientId, risk);
            } else {
                log.debug("RiskClient.getRisk(patientId={}) -> no risk found", patientId);
            }
            
            return risk;
        } catch (RestClientResponseException e) {
            log.error("RiskClient.getRisk(patientId={}) -> risk service unavailable", patientId, e);
            throw new RiskServiceUnavailableException("Risk service unavailable", e);
        }
    }
}
