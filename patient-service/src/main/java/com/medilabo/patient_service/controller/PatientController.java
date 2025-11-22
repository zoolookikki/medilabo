package com.medilabo.patient_service.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medilabo.patient_service.dto.PatientRequestDTO;
import com.medilabo.patient_service.dto.PatientResponseDTO;
import com.medilabo.patient_service.service.PatientService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController 
@Log4j2
@RequestMapping("/patients") 
// Coupler @RequiredArgsConstructor avec des champs final pour rendre les dépendances immuables ==> mieux que @Autowired devenu obsolète.
@RequiredArgsConstructor
@Validated
public class PatientController {
    
    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients() {
        log.debug("GET/patients");
        // bonne pratique : si vide, pas de 204 => un 200 avec liste vide.
        return ResponseEntity.ok(patientService.getAllPatients());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> getPatientById(@PathVariable @Positive Long id) {
        log.debug("GET/patients(id),id="+id);
        Optional<PatientResponseDTO> patient = patientService.getById(id);

        if (patient.isPresent()) {
            log.info("GET/patients(id) : getById found");
            return ResponseEntity.ok(patient.get());
        } else {
            log.info("GET/patients(id) : getById not found");
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<PatientResponseDTO> createPatient(@Valid @RequestBody PatientRequestDTO req){
        log.debug("POST/patients"+ " : "+req);
        PatientResponseDTO createdPatient = patientService.create(req);
        log.info("POST/patients : create ok");
        return ResponseEntity
                .status(HttpStatus.CREATED) // 201 created => preferable to 200 ok.
                .body(createdPatient); // to respect the standard.
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable @Positive Long id, @Valid @RequestBody PatientRequestDTO req) {
        log.debug("PUT/patients(id),id="+id+ " : "+req);
        Optional<PatientResponseDTO> updatedPatient = patientService.update(id, req);

        if (updatedPatient.isPresent()) {
            log.info("PUT/patients : update ok");
            return ResponseEntity.ok(updatedPatient.get());
        } else {
            log.info("PUT/patients : update not ok");
            return ResponseEntity.notFound().build();
        }
    }
}

