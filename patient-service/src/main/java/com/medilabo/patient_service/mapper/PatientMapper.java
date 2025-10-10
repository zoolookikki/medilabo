package com.medilabo.patient_service.mapper;

import org.mapstruct.Mapper;

import com.medilabo.patient_service.dto.PatientRequestDTO;
import com.medilabo.patient_service.dto.PatientResponseDTO;
import com.medilabo.patient_service.entity.Patient;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    Patient requestDTOToEntity(PatientRequestDTO req);
    PatientResponseDTO entityToResponseDTO(Patient entity);
}
