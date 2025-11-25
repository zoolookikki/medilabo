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

/**
 * REST controller that exposes operations for managing patients.
 *
 * <p>
 * This controller provides the HTTP endpoints consumed by other microservices (gateway-service, risk-service) and webapp.
 * </p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Retrieve the full list of patients</li>
 *   <li>Retrieve a patient by its identifier</li>
 *   <li>Create a new patient with validation</li>
 *   <li>Update an existing patient</li>
 * </ul>
 *
 * @see PatientService
 * @see PatientRequestDTO
 * @see PatientResponseDTO
 */
@RestController 
@Log4j2
@RequestMapping("/patients") 
// Coupler @RequiredArgsConstructor avec des champs final pour rendre les dépendances immuables ==> mieux que @Autowired devenu obsolète.
@RequiredArgsConstructor
@Validated
public class PatientController {
    
    private final PatientService patientService;

    /**
     * Retrieves the full list of patients.
     *
     * <p>
     * Returns {@code 200 OK} with an empty list if no patients exist.
     * Returning an empty list instead of {@code 204 No Content} is considered a best practice.
     * </p>
     *
     * @return a list of {@link PatientResponseDTO} wrapped in a {@link ResponseEntity}
     */
    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients() {
        log.debug("GET/patients");
        // bonne pratique : si vide, pas de 204 => un 200 avec liste vide.
        return ResponseEntity.ok(patientService.getAllPatients());
    }
    
    /**
     * Retrieves a patient by its identifier.
     *
     * @param id the patient identifier (must be a positive number)
     * @return {@code 200 OK} with the patient data if found, otherwise {@code 404 NOT FOUND}
     */
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

    /**
     * Creates a new patient.
     *
     * <p>
     * The incoming JSON body is validated using Jakarta Bean Validation.
     * If validation fails, a {@code 400 BAD REQUEST} is returned.
     * </p>
     *
     * @param req the DTO containing patient information, validated by {@code @Valid}
     * @return {@code 201 CREATED} with the created patient
     */
    @PostMapping
    public ResponseEntity<PatientResponseDTO> createPatient(@Valid @RequestBody PatientRequestDTO req){
        log.debug("POST/patients"+ " : "+req);
        PatientResponseDTO createdPatient = patientService.create(req);
        log.info("POST/patients : create ok");
        return ResponseEntity
                .status(HttpStatus.CREATED) // 201 created => preferable to 200 ok.
                .body(createdPatient); // to respect the standard.
    }

    /**
     * Updates an existing patient by replacing all fields with the provided values.
     *
     * <p>
     * If the patient does not exist, returns {@code 404 NOT FOUND}.
     * Otherwise, returns the updated patient with {@code 200 OK}.
     * </p>
     *
     * @param id the identifier of the patient to update (must be positive)
     * @param req the validated patient data
     * @return the updated {@link PatientResponseDTO}, or {@code 404 NOT FOUND} if the patient does not exist
     */
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
