package com.medilabo.webapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.medilabo.webapp.client.NoteClient;
import com.medilabo.webapp.client.PatientClient;
import com.medilabo.webapp.dto.Gender;
import com.medilabo.webapp.dto.NoteRequestDTO;
import com.medilabo.webapp.dto.NoteResponseDTO;
import com.medilabo.webapp.dto.PatientResponseDTO;
import com.medilabo.webapp.exception.NoteServiceUnavailableException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;

@WebMvcTest(NoteController.class)
class NoteControllerTest {

    // @Autowired pour Junit5, c'est plus simple.
    @Autowired MockMvc mvc;
    @MockitoBean PatientClient patientClient;
    @MockitoBean NoteClient noteClient;

    private PatientResponseDTO patientResponseDTO1;
    private NoteResponseDTO noteResponseDTO1, noteResponseDTO2;

    @BeforeEach
    void setUp() {
        patientResponseDTO1 = new PatientResponseDTO();
        patientResponseDTO1.setId(1L);
        patientResponseDTO1.setLastName("Doe");
        patientResponseDTO1.setFirstName("John");
        patientResponseDTO1.setBirthDate(LocalDate.of(1963,1,1));
        patientResponseDTO1.setGender(Gender.MALE);
        patientResponseDTO1.setAddress("1 Main Street");
        patientResponseDTO1.setPhoneNumber("123-456-7890");

        noteResponseDTO1 = new NoteResponseDTO();
        noteResponseDTO1.setId("1");
        noteResponseDTO1.setPatientId(1L);
        noteResponseDTO1.setContent("Line 1\nLine 2");
        noteResponseDTO1.setCreatedAt(Instant.parse("2025-01-01T09:00:00Z"));
        noteResponseDTO2 = new NoteResponseDTO();
        noteResponseDTO2.setId("2");
        noteResponseDTO2.setPatientId(1L);
        noteResponseDTO2.setContent("Last note");
        noteResponseDTO2.setCreatedAt(Instant.parse("2025-02-01T18:00:00Z"));
        }

    @Test
    @DisplayName("Affichage de la liste des notes pour un patient")
    void displayList() throws Exception {
        when(patientClient.findById(1L)).thenReturn(patientResponseDTO1);
        when(noteClient.findByPatientId(1L)).thenReturn(List.of(noteResponseDTO2, noteResponseDTO1)); 

        mvc.perform(get("/notes/patient/1"))
           .andExpect(status().isOk())
           .andExpect(view().name("note/list"))
           .andExpect(model().attributeExists("patient"))
           .andExpect(model().attributeExists("notes"));
    }

    @Test
    @DisplayName("Liste notes vide")
    void displayEmptyList() throws Exception {
        when(patientClient.findById(1L)).thenReturn(patientResponseDTO1);
        when(noteClient.findByPatientId(1L)).thenReturn(List.of());

        mvc.perform(get("/notes/patient/1"))
           .andExpect(status().isOk())
           .andExpect(view().name("note/list"))
           .andExpect(model().attributeExists("patient"))
           .andExpect(model().attributeExists("notes"));
    }
    
    @Test
    @DisplayName("Affichage du formulaire pour la création")
    void showCreateForm_ok() throws Exception {
        when(patientClient.findById(1L)).thenReturn(patientResponseDTO1);

        mvc.perform(get("/notes/add/1"))
           .andExpect(status().isOk())
           .andExpect(view().name("note/edit"))
           .andExpect(model().attributeExists("patient"))
           .andExpect(model().attributeExists("note"));
    }

    @Test
    @DisplayName("Création réussie")
    void createOK() throws Exception {
        when(noteClient.create(any(NoteRequestDTO.class))).thenReturn(noteResponseDTO1);

        mvc.perform(post("/notes")
                .param("patientId", "1")
                .param("content", "Line 1\nLine 2"))
           .andExpect(redirectedUrl("/notes/patient/1"))
           .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("Création en échec")
    void createHS() throws Exception {
        // il faut aussi réinjecter le patient quand le formulaire est réaffiché
        when(patientClient.findById(1L)).thenReturn(patientResponseDTO1);

        mvc.perform(post("/notes")
                .param("patientId", "1")
                .param("content", ""))
           .andExpect(status().isOk())
           .andExpect(view().name("note/edit"))
           .andExpect(model().hasErrors())
           .andExpect(model().errorCount(1))
           // le 1er argument est le nom de l’attribut de modèle.
           .andExpect(model().attributeHasFieldErrors("note", "content"));
    }
    
    @Test
    @DisplayName("Erreur du service Note quand affichage de la liste des notes pour un patient")
    void displayListNoteServiceError() throws Exception {
        when(patientClient.findById(1L)).thenReturn(patientResponseDTO1);
        when(noteClient.findByPatientId(1L)).thenThrow(new NoteServiceUnavailableException("Note service unavailable")); 

        mvc.perform(get("/notes/patient/1"))
           .andExpect(status().isOk())
           .andExpect(view().name("error"))
           .andExpect(model().attribute("message",
                   org.hamcrest.Matchers.containsString("Note service unavailable")));
    }
}
