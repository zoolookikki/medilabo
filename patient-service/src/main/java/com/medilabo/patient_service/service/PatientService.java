package com.medilabo.patient_service.service;

import java.util.List;
import java.util.Optional;

import com.medilabo.patient_service.dto.PatientRequestDTO;
import com.medilabo.patient_service.dto.PatientResponseDTO;

/**
 * Service interface defining operations for managing patients.
 *
 * @see PatientServiceImpl
 * @see PatientRequestDTO
 * @see PatientResponseDTO
 */
public interface PatientService {
    /**
     * Retrieves all patients.
     *
     * @return a list of {@link PatientResponseDTO}
     */
    List<PatientResponseDTO> getAllPatients();
    
    /**
     * Retrieves a patient by its identifier.
     *
     * @param id the patient identifier
     * @return an {@code Optional} containing the patient if found, otherwise empty
     */
    Optional<PatientResponseDTO> getById(Long id);
    
    /**
     * Creates a new patient using the provided request data.
     *
     * @param req the data needed to create the patient
     * @return the created patient as a {@link PatientResponseDTO}
     */
    PatientResponseDTO create(PatientRequestDTO req);
    
    /**
     * Updates an existing patient with the provided request data.
     *
     * @param id the identifier of the patient to update
     * @param req the modified patient data
     * @return an {@code Optional} containing the updated patient, or empty if not found
     */
    Optional<PatientResponseDTO> update(Long id, PatientRequestDTO req);
}
