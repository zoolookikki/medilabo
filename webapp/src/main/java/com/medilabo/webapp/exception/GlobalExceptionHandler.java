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
    
    // Patient injoignable
    @ExceptionHandler(PatientServiceUnavailableException.class)
    public String handlePatientDown(PatientServiceUnavailableException ex, Model model) {
        model.addAttribute("message", "Patient service unavailable. Please try again later.");
        return "error";
    }    

    // Cas général.
    @ExceptionHandler(Exception.class)
    public String handleAny(Exception ex, Model model) {
      model.addAttribute("message", "Unexpected error. Please try again later.");
      return "error";
    }
}
