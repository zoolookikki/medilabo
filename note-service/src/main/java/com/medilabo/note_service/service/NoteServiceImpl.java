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

@Service
//Coupler @RequiredArgsConstructor avec des champs final pour rendre les dépendances immuables ==> mieux que @Autowired devenu obsolète.
@RequiredArgsConstructor
@Log4j2
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    @Override
    public Optional<NoteResponseDTO> getById(String id) {
        Optional<Note> noteOpt = noteRepository.findById(id);
        if (noteOpt.isPresent()) {
            NoteResponseDTO dto = noteMapper.documentToResponseDTO(noteOpt.get());
            return Optional.of(dto);
        }
        return Optional.empty();
    }
    
    @Override
    public List<NoteResponseDTO> getNotesByPatientId(Long patientId) {
        List<Note> notes = noteRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
        List<NoteResponseDTO> result = new ArrayList<>();

        for (Note note : notes) {
            result.add(noteMapper.documentToResponseDTO(note));
        }

        return result;
    }
    
    @Override
    public NoteResponseDTO create(NoteRequestDTO request) {
        Note saved = noteRepository.save(noteMapper.requestDTOToDocument(request));
        return noteMapper.documentToResponseDTO(saved);
    }
}
