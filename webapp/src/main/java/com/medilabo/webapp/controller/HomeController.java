package com.medilabo.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for handling the application’s home.
 *
 * <p>
 * This controller centralizes entry-point redirection for the web interface.  
 * Whenever a user accesses the root path {@code "/"} or {@code "/home"},  
 * they are automatically redirected to the patient list page.
 * </p>
 *
 */
@Controller
public class HomeController {
    /**
     * Redirects to the main patient list view.
     *
     * @return a Spring MVC redirect instruction to {@code "/patients"}
     */
    @GetMapping({"/", "/home"})
    public String home() {
        return "redirect:/patients";
    }
}
