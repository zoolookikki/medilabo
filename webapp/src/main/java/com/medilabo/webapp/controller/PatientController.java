package com.medilabo.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.medilabo.webapp.client.PatientClient;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/patients")
//Coupler @RequiredArgsConstructor avec des champs final pour rendre les dépendances immuables ==> mieux que @Autowired devenu obsolète.
@RequiredArgsConstructor
public class PatientController {
    
    private final PatientClient patientClientApi;

    @GetMapping
    public String list(Model model) {
      model.addAttribute("patients", patientClientApi.findAll());
      return "patient/list"; 
    }
}
