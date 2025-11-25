package com.medilabo.webapp.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Custom Bean Validation annotation used to validate a birth date.
 *
 * <p>This constraint verifies that:</p>
 * <ul>
 *   <li>the date is not in the future,</li>
 *   <li>the resulting age does not exceed a configurable maximum (120 years by default).</li>
 * </ul>
 *
 * <p>Example usage:</p>
 *
 * <pre>{@code
 * @BirthDate(maxAge = 120, message = "Birthdate must represent an age between 0 and 120 years")
 * private LocalDate birthDate;
 * }</pre>
 *
 * <p>
 * As with all Bean Validation constraints, the {@code groups} and {@code payload} attributes are
 * required by the specification, even if not used.
 * </p>
 *
 * @see BirthDateValidator
 */

// Voir chapitre 111.2.5 Le développement de contraintes personnalisées : https://www.jmdoudoux.fr/java/dej/chap-validation_donnees.htm

// Conseillé pour que cela apparaisse dans la documentation javadoc comme pour les autres annotations standards (@NotNull, @Email, @Past, etc...).
@Documented
// Cette annotation ne peut être appliquée que sur un champ d'une classe (ce qui est le cas pour la dto qui l'utilise).
@Target({ FIELD })
// Indique que l’annotation doit être conservée et lisible à l’exécution.
// C’est obligatoire pour que Hibernate Validator puisse la lire et déclencher la validation.
@Retention(RUNTIME)
// Déclare qu’il s’agit d’une contrainte Bean Validation et que pour valider l'annotation, il faut utiliser la classe BirthDateValidator. 
@Constraint(validatedBy = BirthDateValidator.class)
// Pour déclarer une annotation personnalisée.
public @interface BirthDate {
    
    // Message par défaut qui sera utilisé si la validation échoue => à redéfinir éventuellement : @BirthDate(message = "Birthdate must be ...")
    String message() default "Birthdate is invalid";
    
    // Age maximum acceptable par défaut => à redéfinir éventuellement : @BirthDate(maxAge = ...)
    int maxAge() default 120;
    
    // Obligatoire pour les contraintes Bean Validation (même si on ne les utilise pas)
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};    
}    
    
