package com.medilabo.patient_service.service;

import java.util.List;
import java.util.Optional;

import com.medilabo.patient_service.dto.PatientRequestDTO;
import com.medilabo.patient_service.dto.PatientResponseDTO;

public interface PatientService {
    List<PatientResponseDTO> getAllPatients();
    Optional<PatientResponseDTO> getById(Long id);
    PatientResponseDTO create(PatientRequestDTO req);
    Optional<PatientResponseDTO> update(Long id, PatientRequestDTO req);
}
