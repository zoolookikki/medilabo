package com.medilabo.webapp.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.medilabo.webapp.constraints.BirthDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PatientRequestDTO {
//    @NotNull
//    private Long id;
    
    @NotBlank(message = "Last name is mandatory")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    
    @NotBlank(message = "First name is mandatory")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    @NotNull(message = "Birth date is mandatory")
    /*
    Validation métier personnalisée de la date de naissance.
    Vérifie que la date est réaliste : 
        - pas dans le futur,
        - ne dépasse pas l’âge maximum autorisé (120 ans par défaut).
    Evite par exemple d'utiliser @Past avec un contrôle dans le service.
    */
    @BirthDate(message = "Birthdate must correspond to an age between 0 and 120 years")
    // Nécessaire également lors d'une modification (si champs en erreur).
    // Utilisation de l’ISO, soit le format yyyy-MM-dd afin de garantir que Spring formatera correctement la valeur dans les formulaires <input type="date">
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;
    
    @NotNull(message = "Gender is mandatory")
    private Gender gender;
    
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Pattern(
            regexp = "^$|^\\d{3}-\\d{3}-\\d{4}$",
            message = "Phone must be empty or in the format XXX-XXX-XXXX (digits only)"
    )
    private String phoneNumber;
}
