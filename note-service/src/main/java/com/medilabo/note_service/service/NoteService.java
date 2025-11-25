package com.medilabo.note_service.service;

import java.util.List;
import java.util.Optional;

import com.medilabo.note_service.dto.NoteRequestDTO;
import com.medilabo.note_service.dto.NoteResponseDTO;

/**
 * Service interface defining the operations related to notes.
 *
 * <p>
 * It exposes operations to:
 * <ul>
 *   <li>retrieve a note by its unique identifier,</li>
 *   <li>retrieve all notes associated with a given patient,</li>
 *   <li>create a new note.</li>
 * </ul>
 * </p>
 *
 */
public interface NoteService {
    /**
     * Retrieves a note by its unique identifier.
     *
     * @param id the identifier of the note
     * @return an {@link Optional} containing the {@link NoteResponseDTO} if found,
     *         or {@code Optional.empty()} if no note matches the given ID
     */    
    Optional<NoteResponseDTO> getById(String id);
    

    /**
     * Retrieves all notes associated with the given patient ID.
     * <p>
     * Returned notes are already sorted in descending order of creation date by the repository layer.
     * </p>
     *
     * @param patientId the identifier of the patient
     * @return a list of {@link NoteResponseDTO}; may be empty if the patient has no notes
     */    
    List<NoteResponseDTO> getNotesByPatientId(Long patientId);
    
    /**
     * Creates a new note based on the provided request data.
     *
     * @param request the DTO containing the note content and patient ID
     * @return the {@link NoteResponseDTO} including its generated identifier
     */    
    NoteResponseDTO create(NoteRequestDTO request);
}
