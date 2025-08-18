package com.medilabo.patient_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.medilabo.patient_service.dto.PatientRequestDTO;
import com.medilabo.patient_service.dto.PatientResponseDTO;
import com.medilabo.patient_service.mapper.PatientMapper;
import com.medilabo.patient_service.model.Patient;
import com.medilabo.patient_service.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor
public class PatientService {
    
  private final PatientRepository patientRepository;
  private final PatientMapper patientMapper;

  public List<PatientResponseDTO> getAllPatients() {
      List<Patient> patients = patientRepository.findAll();
      List<PatientResponseDTO> result = new ArrayList<>();

      for (Patient patient : patients) {
          result.add(patientMapper.entityToResponseDTO(patient));
      }
      return result;
  }
  
  public Optional<PatientResponseDTO> getById(Long id) {
      Optional<Patient> patientOpt = patientRepository.findById(id);
      if (patientOpt.isPresent()) {
          PatientResponseDTO dto = patientMapper.entityToResponseDTO(patientOpt.get());
          return Optional.of(dto);
      }
      return Optional.empty();
  }
  
  public PatientResponseDTO create(PatientRequestDTO req){
      Patient saved = patientRepository.save(patientMapper.requestDTOToEntity(req));
      return patientMapper.entityToResponseDTO(saved);
  }

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
