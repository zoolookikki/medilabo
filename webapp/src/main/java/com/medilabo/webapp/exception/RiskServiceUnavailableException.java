package com.medilabo.webapp.exception;

import java.io.Serial;

/**
 * Exception levée lorsque le service Risk est indisponible
 * (ex: la gateway répond mais ne parvient pas à joindre le microservice risk).
 */
public class RiskServiceUnavailableException  extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public RiskServiceUnavailableException(String message) {
        super(message);
    }

    public RiskServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
