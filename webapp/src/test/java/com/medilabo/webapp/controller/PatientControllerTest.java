package com.medilabo.webapp.controller;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.medilabo.webapp.client.PatientClient;
import com.medilabo.webapp.dto.Gender;
import com.medilabo.webapp.dto.PatientRequestDTO;
import com.medilabo.webapp.dto.PatientResponseDTO;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;

@WebMvcTest(PatientController.class)
public class PatientControllerTest {

    // @Autowired pour Junit5, c'est plus simple.
    @Autowired MockMvc mvc;
    @MockitoBean PatientClient patientClient;   

    private PatientResponseDTO patientResponseDTO1;
    
    @BeforeEach
    void setup() {
        patientResponseDTO1 = new PatientResponseDTO();
        patientResponseDTO1.setId(1L);
        patientResponseDTO1.setLastName("Doe");
        patientResponseDTO1.setFirstName("John");
        patientResponseDTO1.setBirthDate(LocalDate.of(1963,1,1));
        patientResponseDTO1.setGender(Gender.MALE);
        patientResponseDTO1.setAddress("1 Main Street");
        patientResponseDTO1.setPhoneNumber("123-456-7890");
    }
    
    @Test
    @DisplayName("Affichage de la liste des patients")
    void displayList() throws Exception {
        when(patientClient.findAll()).thenReturn(List.of(patientResponseDTO1));

        mvc.perform(get("/patients"))
            .andExpect(status().isOk())
            .andExpect(view().name("patient/list"))
            .andExpect(model().attributeExists("patients"));
    }
    
    @Test
    @DisplayName("Affichage du formulaire pour la création")
    void showCreateForm() throws Exception {
        mvc.perform(get("/patients/add"))
            .andExpect(status().isOk())
            .andExpect(view().name("patient/edit"))
            .andExpect(model().attributeExists("patient"))
            .andExpect(model().attribute("isUpdate", false));
    }
    
    @Test
    @DisplayName("Création réussie")
    void createOK() throws Exception {
        when(patientClient.create(any(PatientRequestDTO.class))).thenReturn(patientResponseDTO1);

        mvc.perform(post("/patients")
                .param("lastName", "Doe")
                .param("firstName", "John")
                .param("birthDate", "1963-01-01")
                .param("gender", "MALE"))
         .andExpect(redirectedUrl("/patients"))
         .andExpect(flash().attributeExists("successMessage"));
    }
    
    @Test
    @DisplayName("Création en échec")
    void createHS() throws Exception {
        // les 4 champs obligatoire.
        mvc.perform(post("/patients") 
                .param("lastName", "")
                .param("firstName", "")
                .param("birthDate", "")
                .param("gender", ""))
            .andExpect(status().isOk())
            .andExpect(view().name("patient/edit"))
            .andExpect(model().hasErrors())
            .andExpect(model().errorCount(4))
            // le 1er argument est le nom de l’attribut de modèle.
            .andExpect(model().attributeHasFieldErrors(
                    "patient", "lastName", "firstName", "birthDate", "gender"
            ));
    }
    
    @Test
    @DisplayName("Affichage du formulaire pour la modification")
    void showUpdateForm() throws Exception {
        when(patientClient.findById(patientResponseDTO1.getId())).thenReturn(patientResponseDTO1);

        mvc.perform(get("/patients/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("patient/edit"))
            .andExpect(model().attributeExists("patient"))
            .andExpect(model().attribute("isUpdate", true))
            .andExpect(model().attribute("updateId", patientResponseDTO1.getId()));
    }
    
    @Test
    @DisplayName("Mise à jour réussie")
    void updateOK() throws Exception {
        when(patientClient.update(eq(1L), any(PatientRequestDTO.class))).thenReturn(patientResponseDTO1);

        mvc.perform(post("/patients/1/edit")
                .param("lastName", "Doee")
                .param("firstName", "Johnny")
                .param("birthDate", "1963-01-02")
                .param("gender", "UNKNOW"))
            .andExpect(redirectedUrl("/patients"))
            .andExpect(flash().attributeExists("successMessage"));
    }
    
    @Test
    @DisplayName("Modification en échec")
    void updateHS() throws Exception {
        mvc.perform(post("/patients/1/edit")
                .param("lastName", "")
                .param("firstName", "")
                .param("birthDate", "")
                .param("gender", "XYZ")
                .param("phoneNumber", "ab12"))
            .andExpect(status().isOk())
            .andExpect(view().name("patient/edit"))
            .andExpect(model().hasErrors())
            .andExpect(model().errorCount(5))
            // le 1er argument est le nom de l’attribut de modèle.
            .andExpect(model().attributeHasFieldErrors(
                    "patient", "lastName", "firstName", "birthDate", "gender", "phoneNumber"
            ));
    }
}
