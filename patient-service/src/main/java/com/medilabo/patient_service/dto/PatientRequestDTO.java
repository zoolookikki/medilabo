package com.medilabo.patient_service.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medilabo.patient_service.model.Patient;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
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
    
    @NotNull(message = "Birth date is mandatory")
    // pour valider qu’une date se situe dans le passé par rapport à la date actuelle.
    @Past(message = "Birth date must be in the past")
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
            regexp = "^\\d{3}-\\d{3}-\\d{4}$",
            message = "Phone number must be in format XXX-XXX-XXXX"
    )
    @Size(max = 12, message = "Phone number must not exceed 12 characters")
    private String phoneNumber;
}

