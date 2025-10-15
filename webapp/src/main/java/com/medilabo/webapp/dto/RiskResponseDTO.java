package com.medilabo.webapp.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RiskResponseDTO {
    private Long patientId;
    
    private RiskLevel riskLevel;
}
