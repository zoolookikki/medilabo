package com.medilabo.note_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.medilabo.note_service.document.Note;
import com.medilabo.note_service.dto.NoteRequestDTO;
import com.medilabo.note_service.dto.NoteResponseDTO;
import com.medilabo.note_service.mapper.NoteMapper;
import com.medilabo.note_service.repository.NoteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Implementation of the {@link NoteService} interface responsible for  handling the logic related to notes.
 *
 * <p>
 * This service acts as the intermediary between:
 * <ul>
 *   <li>the controller layer, which receives HTTP requests, and</li>
 *   <li>the repository layer, which interacts with MongoDB.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Responsibilities of this class include:
 * <ul>
 *   <li>retrieving a note by its ID,</li>
 *   <li>retrieving all notes for a given patient (already sorted by creation date),</li>
 *   <li>creating and storing new notes in MongoDB.</li>
 * </ul>
 * </p>
 *
 */
@Service
//Coupler @RequiredArgsConstructor avec des champs final pour rendre les dépendances immuables ==> mieux que @Autowired devenu obsolète.
@RequiredArgsConstructor
@Log4j2
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    /**
     * <p>
     * The repository returns a MongoDB {@code Note} document, which is then
     * converted to a {@code NoteResponseDTO} using the mapper.
     * </p>
     */
    @Override
    public Optional<NoteResponseDTO> getById(String id) {
        Optional<Note> noteOpt = noteRepository.findById(id);
        if (noteOpt.isPresent()) {
            NoteResponseDTO dto = noteMapper.documentToResponseDTO(noteOpt.get());
            return Optional.of(dto);
        }
        return Optional.empty();
    }
    
    /**
     * <p>
     * The repository query automatically sorts the notes in descending order
     * based on the {@code createdAt} field. Each MongoDB document is converted
     * to a DTO before returning the result list.
     * </p>
     */    
    @Override
    public List<NoteResponseDTO> getNotesByPatientId(Long patientId) {
        List<Note> notes = noteRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
        List<NoteResponseDTO> result = new ArrayList<>();

        for (Note note : notes) {
            result.add(noteMapper.documentToResponseDTO(note));
        }

        return result;
    }
    
    /**
     * <p>
     * A new note is created by converting the incoming DTO into a MongoDB
     * document, saving it to the database, and returning the persisted
     * representation as a {@code NoteResponseDTO}.
     * </p>
     */    
    @Override
    public NoteResponseDTO create(NoteRequestDTO request) {
        Note saved = noteRepository.save(noteMapper.requestDTOToDocument(request));
        return noteMapper.documentToResponseDTO(saved);
    }
}
