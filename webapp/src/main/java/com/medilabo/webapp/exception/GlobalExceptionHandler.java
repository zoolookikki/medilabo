package com.medilabo.webapp.exception;

import java.net.ConnectException;
import java.net.UnknownHostException;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Gateway injoignable
    @ExceptionHandler({ ConnectException.class, UnknownHostException.class, ResourceAccessException.class })
    public String handleGatewayDown(Exception ex, Model model) {    
        model.addAttribute("message", "API gateway is unavailable. Please try again later.");
        return "error";
    }

    // Service Patient injoignable ou autre erreur (par exemple : erreur de programmation concernant l'appel aux API).
    @ExceptionHandler(PatientServiceUnavailableException.class)
    public String handlePatientDown(PatientServiceUnavailableException ex, Model model) {
        String msg = "Patient service unavailable. Please try again later.";

        // pour trouver l'origine de l'exception du client REST
        Throwable cause = ex.getCause();
        // si l'origine de l'exception est une erreur http renvoyée par le client REST.
        if (cause instanceof org.springframework.web.client.RestClientResponseException rce) {
            // si 5xx -> on affiche un message simple et explicite
            if (rce.getStatusCode().is5xxServerError()) {
                msg = "Patient service is not started or unreachable behind the gateway.";
            }
            // si 4xx -> on affiche un message plus précis si disponible
            else if (rce.getStatusCode().is4xxClientError()) {
                String details = rce.getResponseBodyAsString();
                msg = "Patient service error (" + rce.getStatusCode().value() + " " + rce.getStatusText() + ")."
                        + (details == null || details.isBlank() ? "" : " Details: " + details);
            }
        }

        model.addAttribute("message", msg);
        return "error";
    }


    // Cas général.
    @ExceptionHandler(Exception.class)
    public String handleAny(Exception ex, Model model) {
        model.addAttribute("message", "Unexpected error. Please try again later.");
        return "error";
    }
}
