package com.medilabo.webapp.exception;

import java.io.Serial;

/**
 * Exception levée lorsque le service Note est indisponible
 * (ex: la gateway répond mais ne parvient pas à joindre le microservice Note).
 */
public class NoteServiceUnavailableException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public NoteServiceUnavailableException(String message) {
        super(message);
    }

    public NoteServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
