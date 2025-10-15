package com.medilabo.risk_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.medilabo.risk_service.client.NoteClient;
import com.medilabo.risk_service.client.PatientClient;
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
    

    @Override
    public RiskResponseDTO getRisk(Long patientId) {
        
        PatientResponseDTO patientResponseDTO = patientClient.findById(patientId);
        List<NoteResponseDTO> noteResponseDTO = noteClient.findByPatientId(patientId);

        return new RiskResponseDTO(patientId, RiskLevel.NONE);
    }
}
