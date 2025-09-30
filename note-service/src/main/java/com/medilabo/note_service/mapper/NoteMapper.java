package com.medilabo.note_service.mapper;

import org.mapstruct.Mapper;

import com.medilabo.note_service.document.Note;
import com.medilabo.note_service.dto.NoteRequestDTO;
import com.medilabo.note_service.dto.NoteResponseDTO;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    Note requestDTOToDocument(NoteRequestDTO req);
    NoteResponseDTO documentToResponseDTO(Note document);
}
