package com.medilabo.webapp.exception;

/**
 * Exception levée lorsque le service Patient est indisponible
 * (ex: la gateway répond mais ne parvient pas à joindre le microservice patient).
 */
public class PatientServiceUnavailableException extends RuntimeException {

    public PatientServiceUnavailableException(String message) {
        super(message);
    }

    public PatientServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
