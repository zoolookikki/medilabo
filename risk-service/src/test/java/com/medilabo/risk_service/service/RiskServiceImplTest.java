package com.medilabo.risk_service.service;

import com.medilabo.risk_service.client.NoteClient;
import com.medilabo.risk_service.client.PatientClient;
import com.medilabo.risk_service.dto.*;
import com.medilabo.risk_service.model.RiskLevel;

import lombok.extern.log4j.Log4j2;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Log4j2
class RiskriskServiceImplTest {

    @Mock PatientClient patientClient;
    @Mock NoteClient noteClient;

    @InjectMocks RiskServiceImpl riskService; 

    private PatientResponseDTO patient(long id, LocalDate birthDate, Gender gender) {
        var patient = new PatientResponseDTO();
        patient.setId(id);
        patient.setLastName("Doe");
        patient.setFirstName("John");
        patient.setBirthDate(birthDate);
        patient.setGender(gender);
        patient.setAddress("1 Main Street");
        patient.setPhoneNumber("123-456-7890");
        return patient;
    }

    private NoteResponseDTO note(String content) {
        var note = new NoteResponseDTO();
        note.setId("id_note");
        note.setPatientId(1L);
        note.setContent(content);
        note.setCreatedAt(Instant.now());
        return note;
    }
    
    private final List<NoteResponseDTO> NOTES_FOR_TEST = List.of(
            note("Hémoglobine A1C"), note("Microalbumine"), note("Taille"), note("Poids"), note("Fumeur"), note("Fumeuse"),
            note("Anormal"), note("Cholestérol"), note("Vertige"), note("Rechute"), note("Réaction"), note("Anticorps")
    );

    private List<NoteResponseDTO> generateNotes(int triggerCount) {
        List<NoteResponseDTO> result = new ArrayList<>();
        for (int i = 0; i < triggerCount; i++) {
            result.add(NOTES_FOR_TEST.get(i));
        }
        return result;
    }
    
    @Test 
    @DisplayName("Aucune note => NONE")
    void noneWhenEmptyNotes() {
        when(patientClient.findById(1L)).thenReturn(patient(1L, LocalDate.now().minusYears(30), Gender.MALE));
        when(noteClient.findByPatientId(1L)).thenReturn(List.of());
        assertEquals(RiskLevel.NONE, riskService.getRisk(1L).getRiskLevel());
    }

    @Test 
    @DisplayName("0 déclencheur => NONE quel que soit âge/genre")
    void noneWhenNoTrigger() {
        when(patientClient.findById(1L)).thenReturn(patient(1L, LocalDate.now().minusYears(1), Gender.MALE));
        when(noteClient.findByPatientId(1L)).thenReturn(List.of(
                note("xxx xxx xxx"), note("yyy yyy yyy")
                ));
        assertEquals(RiskLevel.NONE, riskService.getRisk(1L).getRiskLevel());
    }

    @Test 
    @DisplayName("1 déclencheur et âge >=30 => NONE")
    void noneWhenAgeGteOrEqual30AndOneTrigger() {
        when(patientClient.findById(1L)).thenReturn(patient(1L, LocalDate.now().minusYears(30), Gender.UNKNOWN));
        when(noteClient.findByPatientId(1L)).thenReturn(generateNotes(1));
        assertEquals(RiskLevel.NONE, riskService.getRisk(1L).getRiskLevel());
    }
    
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5})
    @DisplayName("2 à 5 déclencheurs et âge >=30 => BORDERLINE")
    void borderlineWhenAgeGteOrEqual30And2To5Triggers(int triggerCount) {
        when(patientClient.findById(1L)).thenReturn(patient(1L, LocalDate.now().minusYears(30), Gender.UNKNOWN));
        when(noteClient.findByPatientId(1L)).thenReturn(generateNotes(triggerCount));
        assertEquals(RiskLevel.BORDERLINE, riskService.getRisk(1L).getRiskLevel());
    }

    @ParameterizedTest
    @ValueSource(ints = {6, 7})
    @DisplayName("6 à 7 déclencheurs et âge >=30 => IN_DANGER")
    void inDangerWhenAgeGteOrEqual30And6To7Triggers(int triggerCount) {
        when(patientClient.findById(1L)).thenReturn(patient(1L, LocalDate.now().minusYears(30), Gender.UNKNOWN));
        when(noteClient.findByPatientId(1L)).thenReturn(generateNotes(triggerCount));
        assertEquals(RiskLevel.IN_DANGER, riskService.getRisk(1L).getRiskLevel());
    }
    

    @Test 
    @DisplayName("8 déclencheurs ou plus et âge >=30 => EARLY_ONSET")
    void earlyOnsetWhenAgeGteOrEqual30AndTriggerGteOrEqual8() {
        when(patientClient.findById(1L)).thenReturn(patient(1L, LocalDate.now().minusYears(30), Gender.UNKNOWN));
        when(noteClient.findByPatientId(1L)).thenReturn(generateNotes(8));
        assertEquals(RiskLevel.EARLY_ONSET, riskService.getRisk(1L).getRiskLevel());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    @DisplayName("1 à 2 déclencheurs et âge <30 et MALE => NONE")
    void noneWhenAgeLte30AndMaleAnd1To2Triggers(int triggerCount) {
        when(patientClient.findById(1L)).thenReturn(patient(1L, LocalDate.now().minusYears(29), Gender.MALE));
        when(noteClient.findByPatientId(1L)).thenReturn(generateNotes(triggerCount));
        assertEquals(RiskLevel.NONE, riskService.getRisk(1L).getRiskLevel());
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 4})
    @DisplayName("3 à 4 déclencheurs et âge <30 et MALE => IN_DANGER")
    void inDangerWhenAgeLte30AndMaleAnd3To4Triggers(int triggerCount) {
        when(patientClient.findById(1L)).thenReturn(patient(1L, LocalDate.now().minusYears(29), Gender.MALE));
        when(noteClient.findByPatientId(1L)).thenReturn(generateNotes(triggerCount));
        assertEquals(RiskLevel.IN_DANGER, riskService.getRisk(1L).getRiskLevel());
    }

    @Test 
    @DisplayName("5 déclencheurs ou plus et âge <30 et MALE => EARLY_ONSET")
    void earlyOnsetWhenAgeLte30AndMaleAndTriggerGteOrEqual5() {
        when(patientClient.findById(1L)).thenReturn(patient(1L, LocalDate.now().minusYears(29), Gender.MALE));
        when(noteClient.findByPatientId(1L)).thenReturn(generateNotes(5));
        assertEquals(RiskLevel.EARLY_ONSET, riskService.getRisk(1L).getRiskLevel());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("1 à 3 déclencheurs et âge <30 et FEMALE => IN_DANGER")
    void noneWhenAgeLte30AndFemaleAnd1To3Trigger(int triggerCount) {
        when(patientClient.findById(1L)).thenReturn(patient(1L, LocalDate.now().minusYears(29), Gender.FEMALE));
        when(noteClient.findByPatientId(1L)).thenReturn(generateNotes(triggerCount));
        assertEquals(RiskLevel.NONE, riskService.getRisk(1L).getRiskLevel());
    }
    
    @ParameterizedTest
    @ValueSource(ints = {4, 5, 6})
    @DisplayName("4 à 6 déclencheurs et âge <30 et FEMALE => IN_DANGER")
    void inDangerWhenAgeLte30AndFemaleAnd4To6Trigger(int triggerCount) {
        when(patientClient.findById(1L)).thenReturn(patient(1L, LocalDate.now().minusYears(29), Gender.FEMALE));
        when(noteClient.findByPatientId(1L)).thenReturn(generateNotes(triggerCount));
        assertEquals(RiskLevel.IN_DANGER, riskService.getRisk(1L).getRiskLevel());
    }

    @Test 
    @DisplayName("7 déclencheurs ou plus et âge <30 et FEMALE => EARLY_ONSET")
    void earlyOnsetWhenAgeLt30AndFemaleAndTriggersGteOrEqual7() {
        when(patientClient.findById(1L)).thenReturn(patient(1L, LocalDate.now().minusYears(29), Gender.FEMALE));
        when(noteClient.findByPatientId(1L)).thenReturn(generateNotes(7));
        assertEquals(RiskLevel.EARLY_ONSET, riskService.getRisk(1L).getRiskLevel());
    }

    @Test 
    @DisplayName("Déclencheurs dupliqués à ne compter qu'une seule fois")
    void duplicateTriggersCountOnce() {
        when(patientClient.findById(1L)).thenReturn(patient(1L, LocalDate.now().minusYears(30), Gender.MALE));
        when(noteClient.findByPatientId(1L)).thenReturn(List.of(
                note("Hémoglobine A1C"),note("Hémoglobine A1C"), note("Hémoglobine A1C"), note("Hémoglobine A1C") 
                ));
        // avec 1 seul, à plus de 30 ans => NONE, à 2 => BORDERLINE.
        assertEquals(RiskLevel.NONE, riskService.getRisk(1L).getRiskLevel());
    }
    
    @Test 
    @DisplayName("Déclencheurs insensibles à la casse => 8 déclencheurs ou plus et âge >=30 => EARLY_ONSET")
    void caseInsensitive() {
        when(patientClient.findById(1L)).thenReturn(patient(1L, LocalDate.now().minusYears(30), Gender.MALE));
        when(noteClient.findByPatientId(1L)).thenReturn(List.of(
                note("MicroAlbumine"), note("TAILLE"),
                note("PoidS"), note("FuMeUr"), note("ANORMAL"), 
                note("VERTiges"), note("RECHUTE"), note("AnticorpS")
                ));
        assertEquals(RiskLevel.EARLY_ONSET, riskService.getRisk(1L).getRiskLevel());
    }
    
    @Test
    @Disabled("Pour vérifier tous les cas visuellement")
    void verifyTheLogicOfTheStatement() {
        int[] ages = {29, 30};
        Gender[] genders = {Gender.MALE, Gender.FEMALE, Gender.UNKNOWN};

        log.debug("----- start logic test -----");
        for (int age : ages) {
            for (Gender gender : genders) {
                for (int triggerCount = 0; triggerCount <= 9; triggerCount++) {
                    RiskLevel riskLevel = riskService.determineRiskLevel(triggerCount, age, gender);
                    log.debug("triggerCount="+triggerCount+",age="+age+",gender="+gender+",riskLevel="+riskLevel);
                }
            }
        }
        log.debug("----- end logic test -----");
    }
}
