package com.medilabo.patient_service.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


import org.springframework.http.converter.HttpMessageNotReadableException;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Global exception handler for managing application-level exceptions and providing user-friendly error responses.
 *
 * This class uses Spring's @RestControllerAdvice to intercept exceptions across all controllers and return appropriate HTTP responses with meaningful error messages.
 */
@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    /**
    * To catch exceptions when validation on a request body fails (@NotNull, @NotBlank, @Email etc...).
    *
    */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        log.debug("GlobalExceptionHandler : MethodArgumentNotValidException");
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }
    
    /**
     * To catch deserialization errors when reading the request body 
     * (e.g., invalid date format, invalid enum value, wrong type for a field).
     *
     * This typically occurs before @Valid validations are triggered,
     * when Jackson fails to convert JSON values to the target object fields.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleNotReadable(HttpMessageNotReadableException ex) {
        String field = "unknown";

        // Remonter la chaîne des causes pour trouver un InvalidFormatException ou MismatchedInputException
        Throwable t = ex;
        com.fasterxml.jackson.databind.exc.InvalidFormatException ife = null;
        com.fasterxml.jackson.databind.exc.MismatchedInputException mie = null;

        while (t != null) {
            if (t instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException e) {
                ife = e; break;
            }
            if (t instanceof com.fasterxml.jackson.databind.exc.MismatchedInputException e) {
                mie = e; break;
            }
            t = t.getCause();
        }

        if (ife != null && ife.getPath() != null && !ife.getPath().isEmpty()) {
            field = ife.getPath().get(0).getFieldName();
        } else if (mie != null && mie.getPath() != null && !mie.getPath().isEmpty()) {
            field = mie.getPath().get(0).getFieldName();
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(field, "Invalid value/format"));    
    }
    
    /**
     * To catch exceptions on @Validated which enables validation on parameters with @PathVariable and @RequestParam.
     *
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleValidationException(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations().stream()
                .map(violation -> violation.getMessage())
                .collect(Collectors.joining(", "));  // Concatenate errors into a single string

        log.debug("GlobalExceptionHandler : ConstraintViolationException");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorMessage);
    }
    
    /**
     * To catch exceptions when types are not respected (like xx that are int)
     *
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
//        String parameterName = ex.getName();  // Nom du paramètre (ex: "station", "id", "address")
        /*
        ex.getName() : récupère le nom du paramètre qui a causé l’erreur
        Optional.ofNullable(...).orElse("unknown") : crée un Optional<String> qui contient la valeur ou si null renvoie "unknown"
        Equivalent impératif :
            String name = ex.getName();
            String parameterName = (name != null) ? name : "unknown";
        */
        String parameterName = Optional.ofNullable(ex.getName()).orElse("unknown");        
        
//        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "bad value";
        /*
        ex.getRequiredType() : renvoie un Class<?>
        .map(Class::getSimpleName) : transforme le Class<?> en String
        Equivalent impératif :
            Class<?> type = ex.getRequiredType();
            String requiredType = (type != null) ? type.getSimpleName() : "bad value";
        */
        String requiredType  = Optional.ofNullable(ex.getRequiredType())
                .map(Class::getSimpleName)
                .orElse("bad value");

        String errorMessage = String.format(
                "The '%s' parameter must be of type '%s'.",
                parameterName,
                requiredType
        );

        log.debug("GlobalExceptionHandler : MethodArgumentTypeMismatchException");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorMessage);
    }
    
    // pour que l'erreur soit idem des 2 cotés : via PostMan et via le test d'intégration (sinon le test d'intégration échoue).
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(Map.of("error", "Internal error"));
    }
    
    /**
     * To recover exceptions when the parameters of a request like http://....? are absent.
     *
     */
/*    
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParams(MissingServletRequestParameterException ex) {
        String paramName = ex.getParameterName();
        String errorMessage = "The parameter '" + paramName + "' is required.";
        
        log.debug("GlobalExceptionHandler : MissingServletRequestParameterException");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorMessage);
    }
*/
}
