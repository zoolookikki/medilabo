package com.medilabo.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.medilabo.webapp.client.PatientClient;
import com.medilabo.webapp.client.RiskClient;
import com.medilabo.webapp.dto.PatientRequestDTO;
import com.medilabo.webapp.dto.PatientResponseDTO;
import com.medilabo.webapp.dto.RiskResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * MVC controller for displaying, creating, and updating patient records.
 *
 * <p>
 * This controller interacts with two backend microservices:
 * </p>
 * <ul>
 *     <li><strong>Patient microservice</strong> — to retrieve, create and update patient data,</li>
 *     <li><strong>Risk microservice</strong> — to compute the associated medical risk level.</li>
 * </ul>
 *
 */
@Controller
@RequestMapping("/patients")
//Coupler @RequiredArgsConstructor avec des champs final pour rendre les dépendances immuables ==> mieux que @Autowired devenu obsolète.
@RequiredArgsConstructor
@Log4j2
public class PatientController {

    private final PatientClient patientClientApi;
    private final RiskClient riskClientApi;

    /**
     * Displays the list of all patients.
     *
     * @param model the MVC model used to populate the view
     * @return the Thymeleaf template {@code patient/list}
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("patients", patientClientApi.findAll());
        return "patient/list"; 
    }

    /**
     * Displays an empty form for creating a new patient.
     *
     * @param model the MVC model used to populate the form
     * @return the Thymeleaf template {@code patient/edit}
     */
    @GetMapping("/add")
    public String showCreateForm(Model model) {
        model.addAttribute("patient", new PatientRequestDTO());
        model.addAttribute("isUpdate", false);
        return "patient/edit";
    }

    /**
     * Handles form submission when creating a new patient.
     *
     * @param patientRequestDTO the form data
     * @param result contains validation errors if any
     * @param redirectAttributes used to carry success messages after redirect
     * @return a redirect to {@code /patients} or the {@code patient/edit} view on error
     */    
    @PostMapping
    public String submitCreateForm(@Valid @ModelAttribute("patient") PatientRequestDTO patientRequestDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        log.debug("submitCreateForm : "+result.hasErrors());
        if (result.hasErrors()) {
            log.warn("submitCreateForm -> {} error(s)", result.getErrorCount());
            result.getFieldErrors().forEach(e ->
            log.warn("Field error: {} - {} (rejected value: {})",
                    e.getField(), e.getDefaultMessage(), e.getRejectedValue())
                    );
            result.getGlobalErrors().forEach(e ->
            log.warn("Global error: {} - {}", e.getObjectName(), e.getDefaultMessage())
                    );            // on ré-affiche le formulaire avec les erreurs
            return "patient/edit"; 
        }
        patientClientApi.create(patientRequestDTO);
        // pour afficher le message sur la liste des patients : données valables uniquement dans la requête suivante après un redirect. Stocké en session temporaire.
        redirectAttributes.addFlashAttribute("successMessage", "Patient successfully created.");
        return "redirect:/patients";
    }    

    /**
     * Displays the update form pre-filled with an existing patient’s data.
     *
     * @param id the ID of the patient to update
     * @param model the MVC model used to populate the view
     * @return the Thymeleaf template {@code patient/edit}
     */    
    @GetMapping("/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        PatientResponseDTO patientResponseDTO = patientClientApi.findById(id);
        model.addAttribute("patient", patientResponseDTO);
        RiskResponseDTO riskResponseDTO = riskClientApi.getRisk(id);
        model.addAttribute("riskLevel", riskResponseDTO.getRiskLevel());
        log.debug("riskLevel : "+riskResponseDTO.getRiskLevel());
        model.addAttribute("isUpdate", true);
        model.addAttribute("updateId", id); 
        return "patient/edit";
    }    

    /**
     * Handles form submission when updating an existing patient.
     *
     * @param id the ID of the patient being updated
     * @param patientRequestDTO the submitted form data
     * @param result contains Bean Validation errors if any
     * @param redirectAttributes used to store flash messages
     * @param model used when redisplaying the form after errors
     * @return a redirect to {@code /patients} or the {@code patient/edit} view on error
     */    
    @PostMapping("/{id}/edit")
    public String submitUpdateForm(@PathVariable Long id,
            @Valid @ModelAttribute("patient") PatientRequestDTO patientRequestDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,                                   
            Model model) {
        log.debug("submitUpdateForm : "+result.hasErrors());
        if (result.hasErrors()) {
            // on ré-affiche le formulaire avec les erreurs
            model.addAttribute("isUpdate", true);
            model.addAttribute("updateId", id); 
            RiskResponseDTO riskResponseDTO = riskClientApi.getRisk(id);
            model.addAttribute("riskLevel", riskResponseDTO.getRiskLevel());
            return "patient/edit";
        }        
        patientClientApi.update(id, patientRequestDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Patient successfully updated.");
        return "redirect:/patients";
    }
}
