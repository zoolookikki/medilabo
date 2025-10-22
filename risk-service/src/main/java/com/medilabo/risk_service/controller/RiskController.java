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

@RestController 
@RequestMapping("/risk")
//Coupler @RequiredArgsConstructor avec des champs final pour rendre les dépendances immuables ==> mieux que @Autowired devenu obsolète.
@RequiredArgsConstructor
@Log4j2
public class RiskController {
    private final RiskService riskService;
    
    @GetMapping("/{patientId}")
    public ResponseEntity<RiskResponseDTO> getRisk(@PathVariable Long patientId) {
        log.debug("GET/risk(id),patientId="+patientId);
        return ResponseEntity.ok(riskService.getRisk(patientId));
    }
    
    @GetMapping("/simulate500")
    public String simulate500() {
        throw new RuntimeException("Simulation d'erreur interne");
    }    
}
