package com.medilabo.risk_service.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.medilabo.risk_service.client.NoteClient;
import com.medilabo.risk_service.client.PatientClient;
import com.medilabo.risk_service.dto.Gender;
import com.medilabo.risk_service.dto.NoteResponseDTO;
import com.medilabo.risk_service.dto.PatientResponseDTO;
import com.medilabo.risk_service.dto.RiskResponseDTO;
import com.medilabo.risk_service.model.RiskLevel;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
//Coupler @RequiredArgsConstructor avec des champs final pour rendre les dépendances immuables ==> mieux que @Autowired devenu obsolète.
@RequiredArgsConstructor
@Log4j2
public class RiskServiceImpl implements RiskService {
    
    private final PatientClient patientClient;
    private final NoteClient noteClient;

    // Termes déclencheurs utilisés pour l’évaluation du risque.
    private static final List<String> TRIGGERS = List.of(
        "Hémoglobine A1C","Microalbumine","Taille","Poids","Fumeur","Fumeuse",
        "Anormal","Cholestérol","Vertige","Rechute","Réaction","Anticorps"
    );

    // Un déclencheur n'est compté qu'une seule fois (set).
    // Insensible à la casse.
    private int countTriggers(List<NoteResponseDTO> notes) {
        Set<String> foundTriggers = new HashSet<String>();

        for (String trigger : TRIGGERS) {
            for (NoteResponseDTO note : notes) {
                if (note.getContent().toLowerCase().contains(trigger.toLowerCase())) {
                    foundTriggers.add(trigger);
                    // ce n'est pas la peine de continuer car on a trouvé ce trigger dans une des notes.
                    break; 
                }
            }
        }
        
        return foundTriggers.size();
    }    
    
    // Précision sur les règles de l'énoncé :
    // - plus de 30 ans est interprété comme >=30.
    // - pour gender.UNKNOWN on applique la règle la plus stricte soit celle de gender.MALE.

    /*
    ATTENTION : l'énoncé est trompeur => voir test doubtAboutTheLogicOfTheStatement dans RiskriskServiceImplTest
     - <30 ans, H : 3 ⇒ In Danger, 4 ⇒ None !!! , 5+ ⇒ Early Onset
     - <30 ans, F : 4 ⇒ In Danger, 5–6 ⇒ None !!! , 7+ ⇒ Early Onset
    */
/*    
    public RiskLevel determineRiskLevel(int triggerCount, int age, Gender gender) {
        if (triggerCount < 0) throw new IllegalArgumentException("triggerCount must be >= 0 : " + triggerCount);
        if (age < 0)          throw new IllegalArgumentException("age must be >= 0 : " + age);
        if (gender == null)   throw new IllegalArgumentException("gender is null");

        Gender workGender = (gender == Gender.UNKNOWN) ? Gender.MALE : gender;
        RiskLevel riskLevel = RiskLevel.NONE;

        // 1) None : aucun déclencheur.
        if (triggerCount == 0) {
            riskLevel = RiskLevel.NONE;
        }
        // 2) Borderline : de 2 à 5 déclencheurs et âge >= 30.
        else if (age >= 30 && triggerCount >= 2 && triggerCount <= 5) {
            riskLevel = RiskLevel.BORDERLINE;
        }
        // 3) In Danger :
        //    - âge <30 : M => 3 ; F => 4.
        //    - âge >=30 : 6 ou 7.
        else if ((age < 30 && workGender == Gender.MALE   && triggerCount == 3) ||
                 (age < 30 && workGender == Gender.FEMALE && triggerCount == 4) ||
                 (age >= 30 && (triggerCount == 6 || triggerCount == 7))) {
            riskLevel = RiskLevel.IN_DANGER;
        }
        // 4) Early onset :
        //    - âge <30 : M => ≥5 ; F => ≥7.
        //    - âge >=30 : >= 8
        else if ((age < 30 && workGender == Gender.MALE   && triggerCount >= 5) ||
                 (age < 30 && workGender == Gender.FEMALE && triggerCount >= 7) ||
                 (age >= 30 && triggerCount >= 8)) {
            riskLevel = RiskLevel.EARLY_ONSET;
        }
        // tous les autres cas sont NONE (par défaut).

        return riskLevel;
    }
*/
    
    public RiskLevel determineRiskLevel(int triggerCount, int age, Gender gender) {
        if (triggerCount < 0) throw new IllegalArgumentException("triggerCount must be >= 0 : " + triggerCount);
        if (age < 0)          throw new IllegalArgumentException("age must be >= 0 : " + age);
        if (gender == null)   throw new IllegalArgumentException("gender is null");

        RiskLevel riskLevel = RiskLevel.NONE;

        if (triggerCount > 0) {
            if (age >= 30) {
                if (triggerCount >= 8)      riskLevel = RiskLevel.EARLY_ONSET;
                else if (triggerCount >= 6) riskLevel = RiskLevel.IN_DANGER;
                else if (triggerCount >= 2) riskLevel = RiskLevel.BORDERLINE;
            } else {
                switch (gender) {
                case MALE:
                // on applique les mêmes règles que pour MALE.
                case UNKNOWN:
                    if (triggerCount >= 5)      riskLevel = RiskLevel.EARLY_ONSET;
                    else if (triggerCount >= 3) riskLevel = RiskLevel.IN_DANGER;
                    break;
                case FEMALE:
                    if (triggerCount >= 7)      riskLevel = RiskLevel.EARLY_ONSET;
                    else if (triggerCount >= 4)      riskLevel = RiskLevel.IN_DANGER;
                    break;
                }
            }
        }
        return riskLevel;
    }
    
    @Override
    public RiskResponseDTO getRisk(Long patientId) {
        
        PatientResponseDTO patientResponseDTO = patientClient.findById(patientId);
        List<NoteResponseDTO> notesResponseDTO = noteClient.findByPatientId(patientId);

        int triggerCount = countTriggers(notesResponseDTO);
        int age = Period.between(patientResponseDTO.getBirthDate(), LocalDate.now()).getYears();
        RiskLevel riskLevel = determineRiskLevel(triggerCount, age, patientResponseDTO.getGender());
        log.debug("triggerCount="+triggerCount+",age="+age+",gender="+patientResponseDTO.getGender()+",riskLevel="+riskLevel);

        return new RiskResponseDTO(patientId, riskLevel);
    }
}
