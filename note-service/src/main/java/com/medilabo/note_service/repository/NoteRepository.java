package com.medilabo.note_service.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.medilabo.note_service.document.Note;

@Repository
public interface NoteRepository extends MongoRepository<Note, String> {
    // récupérer uniquement les notes d’un patientId et les classer par createdAt décroissant.
    List<Note> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}
