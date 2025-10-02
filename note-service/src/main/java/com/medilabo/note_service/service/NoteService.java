package com.medilabo.note_service.service;

import java.util.List;
import java.util.Optional;

import com.medilabo.note_service.dto.NoteRequestDTO;
import com.medilabo.note_service.dto.NoteResponseDTO;

public interface NoteService {
    Optional<NoteResponseDTO> getById(String id);
    List<NoteResponseDTO> getNotesByPatientId(Long patientId);
    NoteResponseDTO create(NoteRequestDTO request);
}
