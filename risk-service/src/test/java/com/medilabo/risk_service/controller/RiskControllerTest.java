package com.medilabo.risk_service.controller;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.medilabo.risk_service.dto.RiskResponseDTO;
import com.medilabo.risk_service.exception.NoteServiceUnavailableException;
import com.medilabo.risk_service.exception.PatientServiceUnavailableException;
import com.medilabo.risk_service.model.RiskLevel;
import com.medilabo.risk_service.service.RiskService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RiskController.class)
class RiskControllerTest {

    // @Autowired pour Junit5, c'est plus simple.
    @Autowired MockMvc mvc;
    @MockitoBean RiskService riskService;

    @Test
    @DisplayName("Récupération du risque – 200 OK")
    void getRiskOk() throws Exception {
        when(riskService.getRisk(1L)).thenReturn(new RiskResponseDTO(1L, RiskLevel.BORDERLINE));

        mvc.perform(get("/risk/{id}", 1))
           .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Type de paramètre incorrect – 400")
    void getRiskTypeMismatchReturns400() throws Exception {
        mvc.perform(get("/risk/{id}", "xxx"))
           .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Id non trouvé – 404")
    void getRiskIdNotFound() throws Exception {
        when(riskService.getRisk(999L))
                .thenThrow(new org.springframework.web.client.HttpClientErrorException(HttpStatus.NOT_FOUND));

        mvc.perform(get("/risk/{id}", 999))
           .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Service Patient HS – 503")
    void getRiskPatientServiceDown() throws Exception {
        when(riskService.getRisk(1L))
                .thenThrow(new PatientServiceUnavailableException("Patient service unreachable"));

        mvc.perform(get("/risk/{id}", 1))
           .andExpect(status().isServiceUnavailable());
    }
    
    @Test
    @DisplayName("Service Note HS – 503")
    void getRiskNoteServiceDown() throws Exception {
        when(riskService.getRisk(1L))
                .thenThrow(new NoteServiceUnavailableException("Note service unreachable"));

        mvc.perform(get("/risk/{id}", 1))
           .andExpect(status().isServiceUnavailable());
    }

    @Test
    @DisplayName("Internal error – 500")
    void getRiskInternalError() throws Exception {
        when(riskService.getRisk(1L))
                .thenThrow(new RuntimeException("internal error"));

        mvc.perform(get("/risk/{id}", 1))
           .andExpect(status().isInternalServerError());
    }    
}
