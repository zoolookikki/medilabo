package com.medilabo.note_service.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medilabo.note_service.dto.NoteRequestDTO;
import com.medilabo.note_service.dto.NoteResponseDTO;
import com.medilabo.note_service.service.NoteService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@RequestMapping("/notes")
//Coupler @RequiredArgsConstructor avec des champs final pour rendre les dépendances immuables ==> mieux que @Autowired devenu obsolète.
@RequiredArgsConstructor
@Validated
public class NoteController {
    private final NoteService noteService;

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> getNoteById(@PathVariable String id) {
        log.debug("GET/notes(id),id="+id);

        Optional<NoteResponseDTO> note = noteService.getById(id);

        if (note.isPresent()) {
            log.info("GET/notes(id) : getById found");
            return ResponseEntity.ok(note.get());
        } else {
            log.info("GET/notes(id) : getById not found");
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<NoteResponseDTO>> getNotesByPatientId(@PathVariable @Positive Long patientId) {
        log.debug("GET/notes/patient(id),id="+patientId);
        // bonne pratique : si vide, pas de 204 => un 200 avec liste vide.
        return ResponseEntity.ok(noteService.getNotesByPatientId(patientId));
    }
    
    @PostMapping
    public ResponseEntity<NoteResponseDTO> createNote(@Valid @RequestBody NoteRequestDTO request) {
        log.debug("POST/notes"+ " : "+request);
        NoteResponseDTO createdNote = noteService.create(request);
        log.info("POST/notes : create ok");
        return ResponseEntity
                .status(HttpStatus.CREATED) // 201 created => preferable to 200 ok.
                .body(createdNote); // to respect the standard.
    }
    
    @GetMapping("/simulate500")
    public String simulate500() {
        throw new RuntimeException("Simulation d'erreur interne");
    }
}
