package com.medilabo.gateway_service.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GatewayTestController {
    @GetMapping("/api/simulate500")
    public String other_simulate500() {
        throw new RuntimeException("Simulation d'erreur interne");
    }
}
