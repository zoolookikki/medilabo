package com.medilabo.risk_service.service;

import com.medilabo.risk_service.dto.RiskResponseDTO;

public interface RiskService {
    RiskResponseDTO getRisk(Long patientId);
}
