package com.medilabo.webapp.dto;


import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class PatientResponseDTO {
    private Long id;
    
    private String lastName;
    
    private String firstName;
    
    // Utilisation de l’ISO, soit le format yyyy-MM-dd afin de garantir que Spring formatera correctement la valeur dans les formulaires <input type="date">
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;
    
    private String gender;       
    
    private String address;
    
    private String phoneNumber;
}
