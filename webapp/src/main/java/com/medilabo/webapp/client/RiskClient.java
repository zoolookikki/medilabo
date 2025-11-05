package com.medilabo.webapp.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.medilabo.webapp.dto.RiskResponseDTO;
import com.medilabo.webapp.exception.RiskServiceUnavailableException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.client.RestClientResponseException;

@Component
//Coupler @RequiredArgsConstructor avec des champs final pour rendre les dépendances immuables ==> mieux que @Autowired devenu obsolète.
@RequiredArgsConstructor
@Log4j2
public class RiskClient {
    // voir remarque dans NoteClient.
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "Spring RestClient is immutable and thread-safe; no internal state exposure."
    )
    private final RestClient rest;

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
