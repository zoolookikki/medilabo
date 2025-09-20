package com.medilabo.webapp.exception;

import java.io.Serial;

/**
 * Exception levée lorsque le service Patient est indisponible
 * (ex: la gateway répond mais ne parvient pas à joindre le microservice patient).
 */
public class PatientServiceUnavailableException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public PatientServiceUnavailableException(String message) {
        super(message);
    }

    public PatientServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
