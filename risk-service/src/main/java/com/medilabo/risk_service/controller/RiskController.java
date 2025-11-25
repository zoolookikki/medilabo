package com.medilabo.risk_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medilabo.risk_service.dto.RiskResponseDTO;
import com.medilabo.risk_service.service.RiskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * REST controller exposing the Risk API.
 *
 * <p>
 * This controller receives HTTP requests from the WebApp and delegates the computation of the patient's risk level to the {@link RiskService}.  
 * </p>
 *
 * <h2>Endpoint</h2>
 * <ul>
 *     <li><strong>GET /risk/{patientId}</strong> – Computes and returns the medical risk level associated with the given patient.</li>
 * </ul>
 *
 */
@RestController 
@RequestMapping("/risk")
//Coupler @RequiredArgsConstructor avec des champs final pour rendre les dépendances immuables ==> mieux que @Autowired devenu obsolète.
@RequiredArgsConstructor
@Log4j2
public class RiskController {
    /**
     * Service responsible for computing the risk level for a given patient.
     */    
    private final RiskService riskService;
    
    /**
     * Computes and returns the medical risk level for the specified patient.
     *
     * <p>
     * A {@code 200 OK} response is always returned if the service executes normally.
     * </p>
     *
     * @param patientId the identifier of the patient whose risk must be calculated
     * @return a {@code ResponseEntity} containing a {@link RiskResponseDTO} with the risk level
     */    
    @GetMapping("/{patientId}")
    public ResponseEntity<RiskResponseDTO> getRisk(@PathVariable Long patientId) {
        log.debug("GET/risk(id),patientId="+patientId);
        return ResponseEntity.ok(riskService.getRisk(patientId));
    }
}
