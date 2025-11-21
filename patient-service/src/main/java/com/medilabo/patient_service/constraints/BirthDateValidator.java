package com.medilabo.patient_service.constraints;

import java.time.LocalDate;
import java.time.Period;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidator;

// Voir chapitre 111.2.5 Le développement de contraintes personnalisées : https://www.jmdoudoux.fr/java/dej/chap-validation_donnees.htm

// Implémente l’interface ConstraintValidator de Jakarta Validation.
// Génériques : @BirthDate permettra de déclencer le validator, LocalDate le type (si l'on met autre chose lors de l'utilisation => erreur de compilation).
public class BirthDateValidator implements ConstraintValidator<BirthDate, LocalDate> {

    private int maxAge;

    @Override
    public void initialize(BirthDate constraintAnnotation) {
        this.maxAge  = constraintAnnotation.maxAge();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        // On laisse @NotNull gérer le cas null
        if (value == null) {
            return true;
        }

        LocalDate today = LocalDate.now();

        // 1) ne doit pas être dans le futur
        if (value.isAfter(today)) {
            return false;
        }

        // 2) ne doit pas dépasser l'âge max (ex: 120 ans)
        int age = Period.between(value, today).getYears();
        if (age > maxAge) {
            return false;
        }

        return true;
    }
}