package com.medilabo.patient_service.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medilabo.patient_service.model.Patient;

import lombok.Data;

@Data
public class PatientResponseDTO {
    private Long id;
    
    private String lastName;
    
    private String firstName;
    
    @JsonFormat(pattern="yyyy-MM-dd") 
    private LocalDate birthDate;
    
    private Patient.Gender gender;
    
    private String address;
    
    private String phoneNumber;
}
