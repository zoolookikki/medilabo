package com.medilabo.risk_service.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PatientResponseDTO {
    private Long id;
    
    private String lastName;
    
    private String firstName;
    
    private LocalDate birthDate;
    
    private Gender gender;
    
    private String address;
    
    private String phoneNumber;
}
