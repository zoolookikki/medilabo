package com.medilabo.webapp.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.medilabo.webapp.client.NoteClient;
import com.medilabo.webapp.client.PatientClient;
import com.medilabo.webapp.dto.NoteRequestDTO;
import com.medilabo.webapp.dto.NoteResponseDTO;
import com.medilabo.webapp.dto.PatientResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * MVC controller for displaying, creating, and managing notes associated with a specific patient.
 *
 * <p>
 * This controller belongs to the Web application layer (WebApp) and communicates
 * with two backend microservices:
 * </p>
 * <ul>
 *     <li><strong>Patient microservice</strong> — used to retrieve patient details;</li>
 *     <li><strong>Note microservice</strong> — used to retrieve and create medical notes.</li>
 * </ul>
 *
 * <p>
 * It exposes standard MVC endpoints that:
 * </p>
 * <ul>
 *     <li>display a patient's notes,</li>
 *     <li>show a form for creating a new note,</li>
 *     <li>validate and submit note creation.</li>
 * </ul>
 *
 */
@Controller
@RequestMapping("/notes")
//Coupler @RequiredArgsConstructor avec des champs final pour rendre les dépendances immuables ==> mieux que @Autowired devenu obsolète.
@RequiredArgsConstructor
@Log4j2
public class NoteController {
    private final NoteClient noteClientApi;
    private final PatientClient patientClientApi;


    /**
     * Displays all notes associated with a specific patient.
     *
     * @param patientId the ID of the patient whose notes must be displayed
     * @param model the MVC model used to populate the view
     * @return the Thymeleaf template {@code note/list}
     */
    @GetMapping("/patient/{patientId}")
    public String getNotesByPatient(@PathVariable Long patientId, Model model) {
        PatientResponseDTO patient = patientClientApi.findById(patientId);
        List<NoteResponseDTO> notes = noteClientApi.findByPatientId(patientId);
        model.addAttribute("patient", patient);
        model.addAttribute("notes", notes);
        return "note/list";
    }
    
    /**
     * Displays the form used to create a new note for a given patient.
     *
     * @param patientId the ID of the patient for whom the note is created
     * @param model the MVC model used to populate the form
     * @return the Thymeleaf template {@code note/edit}
     */    
    @GetMapping("/add/{patientId}")
    public String showCreateForm(@PathVariable Long patientId, Model model) {
        PatientResponseDTO patient = patientClientApi.findById(patientId);
        NoteRequestDTO note = new NoteRequestDTO();
        model.addAttribute("patient", patient);
        note.setPatientId(patientId);
        model.addAttribute("note", note);
        return "note/edit";
    }    
    
    /**
     * Handles the submission of the note creation form.
     *
     * @param noteRequestDTO the form data submitted by the user
     * @param result contains validation errors, if any
     * @param redirectAttributes used to store flash messages during redirect
     * @param model re-used only when returning the form after validation errors
     * @return a redirect to {@code /notes/patient/{patientId}} upon success,
     *         or the Thymeleaf template {@code note/edit} in case of validation errors
     */        
    @PostMapping
    public String submitCreateForm(@Valid @ModelAttribute("note") NoteRequestDTO noteRequestDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        log.debug("submitCreateForm : "+result.hasErrors());
        if (result.hasErrors()) {
            log.warn("submitCreateForm -> {} error(s)", result.getErrorCount());
            result.getFieldErrors().forEach(e ->
            log.warn("Field error: {} - {} (rejected value: {})",
                    e.getField(), e.getDefaultMessage(), e.getRejectedValue())
                    );
            result.getGlobalErrors().forEach(e ->
            log.warn("Global error: {} - {}", e.getObjectName(), e.getDefaultMessage())
                    );
            // recharger le patient pour l’en-tête du formulaire
            PatientResponseDTO patient = patientClientApi.findById(noteRequestDTO.getPatientId());
            model.addAttribute("patient", patient);
            // on ré-affiche le formulaire avec les erreurs
            return "note/edit";
        }

        noteClientApi.create(noteRequestDTO);
        // pour afficher le message sur la liste des notes : données valables uniquement dans la requête suivante après un redirect. Stocké en session temporaire.
        redirectAttributes.addFlashAttribute("successMessage", "Note successfully added.");
        return "redirect:/notes/patient/" + noteRequestDTO.getPatientId();
    }
}
