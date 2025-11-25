package com.medilabo.patient_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.medilabo.patient_service.dto.PatientRequestDTO;
import com.medilabo.patient_service.dto.PatientResponseDTO;
import com.medilabo.patient_service.entity.Patient;
import com.medilabo.patient_service.mapper.PatientMapper;
import com.medilabo.patient_service.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link PatientService} that handles all logic related to patient management.
 *
 * @see PatientService
 * @see PatientRepository
 * @see PatientMapper
 */
@Service 
//Coupler @RequiredArgsConstructor avec des champs final pour rendre les dépendances immuables ==> mieux que @Autowired devenu obsolète.
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    
  private final PatientRepository patientRepository;
  private final PatientMapper patientMapper;

  /**
   * Retrieves all patients and converts them to response DTOs.
   *
   * @return a list of {@link PatientResponseDTO}
   */
  @Override
  public List<PatientResponseDTO> getAllPatients() {
      List<Patient> patients = patientRepository.findAll();
      List<PatientResponseDTO> result = new ArrayList<>();

      for (Patient patient : patients) {
          result.add(patientMapper.entityToResponseDTO(patient));
      }
      return result;
  }
  
  /**
   * Retrieves a patient by ID and converts it to a DTO.
   *
   * @param id the patient identifier
   * @return an {@code Optional} containing the DTO if found
   */
  @Override
  public Optional<PatientResponseDTO> getById(Long id) {
      Optional<Patient> patientOpt = patientRepository.findById(id);
      if (patientOpt.isPresent()) {
          PatientResponseDTO dto = patientMapper.entityToResponseDTO(patientOpt.get());
          return Optional.of(dto);
      }
      return Optional.empty();
  }
  
  /**
   * Creates and saves a new patient, converting request data into an entity.
   *
   * @param req the data used to create the patient
   * @return the created patient mapped to a DTO
   */  
  @Override
  public PatientResponseDTO create(PatientRequestDTO req){
      Patient saved = patientRepository.save(patientMapper.requestDTOToEntity(req));
      return patientMapper.entityToResponseDTO(saved);
  }

  /**
   * Updates an existing patient by replacing all fields with the provided request data.
   *
   * <p>
   * If the patient does not exist, an empty {@code Optional} is returned.
   * Otherwise the patient is updated and mapped back to a DTO.
   * </p>
   *
   * @param id the identifier of the patient to update
   * @param req the updated patient data
   * @return an {@code Optional} containing the updated DTO
   */  
  @Override
  public Optional<PatientResponseDTO> update(Long id, PatientRequestDTO req) {
      Optional<Patient> existingOpt = patientRepository.findById(id);

      if (existingOpt.isEmpty()) {
          return Optional.empty();
      }

      Patient replaced = patientMapper.requestDTOToEntity(req);
      replaced.setId(existingOpt.get().getId());

      Patient updated = patientRepository.save(replaced);
      return Optional.of(patientMapper.entityToResponseDTO(updated));
  }
}
