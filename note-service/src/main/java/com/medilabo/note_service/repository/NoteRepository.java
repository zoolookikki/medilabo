package com.medilabo.note_service.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.medilabo.note_service.document.Note;

/**
 * Repository interface for accessing and managing {@link Note} documents in MongoDB.
 *
 * <p>
 * This repository extends {@link MongoRepository}, which provides standard CRUD operations
 * for MongoDB documents. Spring Data automatically generates the implementation at runtime.
 * </p>
 *
 */
@Repository
public interface NoteRepository extends MongoRepository<Note, String> {
    /**
     * Retrieves all notes associated with the given patient ID, ordered by the creation date in descending order (newest first).
     *
     * @param patientId the identifier of the patient whose notes should be retrieved
     * @return a list of {@link Note} documents sorted by {@code createdAt} descending;
     *         returns an empty list if the patient has no notes
     */
    List<Note> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}
