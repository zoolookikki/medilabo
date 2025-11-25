package com.medilabo.note_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * MongoDB configuration class enabling support in Spring Data MongoDB.
 *
 * <p>
 * By annotating the class with {@link EnableMongoAuditing}, Spring activates
 * the auditing infrastructure that automatically populates fields annotated
 * with auditing annotations such as:
 * </p>
 *
 * <ul>
 *   <li>{@code @CreatedDate} — sets the creation timestamp when a document is first persisted</li>
 *   <li>{@code @LastModifiedDate} — updates the timestamp whenever the document is modified</li>
 *   <li>{@code @CreatedBy} — optionally records the user who created the document</li>
 *   <li>{@code @LastModifiedBy} — optionally records the user who last modified the document</li>
 * </ul>
 *
 * @see org.springframework.data.mongodb.config.EnableMongoAuditing
 * @see org.springframework.data.annotation.CreatedDate
 * @see org.springframework.data.annotation.LastModifiedDate
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
    // rien à ajouter, l’annotation suffit.
}
