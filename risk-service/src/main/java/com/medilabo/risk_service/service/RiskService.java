package com.medilabo.risk_service.service;

import com.medilabo.risk_service.dto.RiskResponseDTO;

/**
 * Service interface responsible for computing the medical risk level of a patient.
 *
 * <p>
 * This service centralizes the risk-assessment logic used by the Risk microservice.
 * Implementations are expected to:
 * </p>
 * <ul>
 *     <li>retrieve the patient’s data from the Patient microservice,</li>
 *     <li>retrieve the patient’s medical notes from the Note microservice,</li>
 *     <li>analyze the notes for relevant medical keywords,</li>
 *     <li>apply the risk calculation rules,</li>
 *     <li>produce a {@link RiskResponseDTO} describing the computed risk level.</li>
 * </ul>
 *
 */
public interface RiskService {
    /**
     * Computes the medical risk level associated with a specific patient.
     *
     * @param patientId the unique identifier of the patient whose risk must be calculated
     * @return a {@code RiskResponseDTO} containing the computed risk level
     */    
    RiskResponseDTO getRisk(Long patientId);
}
