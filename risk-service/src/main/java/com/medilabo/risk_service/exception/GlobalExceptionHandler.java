package com.medilabo.risk_service.exception;

import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.log4j.Log4j2;


import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {
    
    // Patient indisponible (non lancé / une quelconque erreur dans le service)
    @ExceptionHandler(PatientServiceUnavailableException.class)
    ResponseEntity<Map<String,String>> handlePatientDown(PatientServiceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("message","Patient service unavailable"));
    }

    // Idem pour Note
    @ExceptionHandler(NoteServiceUnavailableException.class)
    ResponseEntity<Map<String,String>> handleNoteDown(NoteServiceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("message","Note service unavailable"));
    }

    // 4xx/5xx retournés par Patient/Note (404 patient inexistant, 400 bad request) -> propager le code
    @ExceptionHandler(org.springframework.web.client.RestClientResponseException.class)
    ResponseEntity<Void> handlePropagation(RestClientResponseException ex) {
        return ResponseEntity.status(ex.getStatusCode()).build();
    }

    /**
     * To catch exceptions when types are not respected (like xx that are int)
     *
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String parameterName = ex.getName();  // Nom du paramètre (ex: "station", "id", "address")
        
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "bad value";

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
    
    // Cas général.
    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String,String>> handleAny() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message","internal error"));
    }   
}
