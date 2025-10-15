package com.medilabo.risk_service.dto;

import com.medilabo.risk_service.model.RiskLevel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RiskResponseDTO {
    private Long patientId;
    
    private RiskLevel riskLevel;
}
