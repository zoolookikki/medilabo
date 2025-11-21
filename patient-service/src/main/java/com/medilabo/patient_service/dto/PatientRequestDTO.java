package com.medilabo.patient_service.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medilabo.patient_service.constraints.BirthDate;
import com.medilabo.patient_service.entity.Patient;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

@Data
public class PatientRequestDTO {
    @NotBlank(message = "Last name is mandatory")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    
    @NotBlank(message = "First name is mandatory")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    @NotNull(message = "Birthdate is mandatory")
    /*
    Validation métier personnalisée de la date de naissance.
    Vérifie que la date est réaliste : 
        - pas dans le futur,
        - ne dépasse pas l’âge maximum autorisé (120 ans par défaut).
    Evite par exemple d'utiliser @Past avec un contrôle dans le service.
    */
    @BirthDate(message = "Birthdate must correspond to an age between 0 and 120 years")
    /*
    Pour que Jackson convertisse le champ en LocalDate uniquement.
    Attention : ne fait pas le contrôle, il est fait au moment de la déssérialiation et donc une exception HttpMessageNotReadableException est lancée. 
    */
    @JsonFormat(pattern="yyyy-MM-dd") 
    private LocalDate birthDate;
    
    @NotNull(message = "Gender is mandatory")
    private Patient.Gender gender;
    
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;
    
    @Pattern(
            regexp = "^$|^\\d{3}-\\d{3}-\\d{4}$",
            message = "Phone must be empty or XXX-XXX-XXXX"
    )
    private String phoneNumber;
}

