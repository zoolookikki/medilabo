package com.medilabo.webapp.exception;

import java.net.ConnectException;
import java.net.UnknownHostException;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.NoHandlerFoundException;

import lombok.extern.log4j.Log4j2;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    
    // Gateway injoignable
    @ExceptionHandler({ ConnectException.class, UnknownHostException.class, ResourceAccessException.class })
    public String handleGatewayDown(Exception ex, Model model) {    
        model.addAttribute("message", "API gateway is unavailable. Please try again later.");
        return "error";
    }

    // Services Patient ou Note ou Risk injoignables ou autre erreur (par exemple : erreur de programmation concernant l'appel aux API).
    @ExceptionHandler({PatientServiceUnavailableException.class, NoteServiceUnavailableException.class, RiskServiceUnavailableException.class})
    public String handleServiceDown(RuntimeException ex, Model model) {
        String serviceName =
                (ex instanceof PatientServiceUnavailableException) ? "Patient" :
                (ex instanceof NoteServiceUnavailableException)    ? "Note"    :
                                                                     "Risk";
        String msg = serviceName + " service unavailable. Please try again later.";

        // pour trouver l'origine de l'exception du client REST
        Throwable cause = ex.getCause();
        // si l'origine de l'exception est une erreur http renvoyée par le client REST.
        if (cause instanceof org.springframework.web.client.RestClientResponseException rce) {

            // si 5xx -> on log uniquement.
            if (rce.getStatusCode().is5xxServerError()) {
                log.warn("{} returned 5xx via gateway: {}", serviceName, rce.getStatusText());
            }
            // si 4xx -> on affiche un message plus précis si disponible
            else if (rce.getStatusCode().is4xxClientError()) {
                String details = rce.getResponseBodyAsString();
                msg = serviceName + " service error (" + rce.getStatusCode().value() + " " + rce.getStatusText() + ")."
                        + (details == null || details.isBlank() ? "" : " Details: " + details);
            }
            
        }

        model.addAttribute("message", msg);
        return "error";
    }
    
    // 404 path error.
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(Exception ex, Model model) {
        model.addAttribute("message", "Page not found.");
        return "error"; 
    }
   
    // Cas général.
    @ExceptionHandler(Exception.class)
    public String handleAny(Exception ex, Model model) {
        model.addAttribute("message", "Unexpected error. Please try again later.");
        return "error";
    }
}
