package com.medilabo.note_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
// active l’audit automatique dans Spring Data MongoDB : les champs annotés (@CreatedDate, @LastModifiedDate, etc.) sont remplis automatiquement lors de la création ou mise à jour d’un document.
@EnableMongoAuditing
public class MongoConfig {
    // rien à ajouter, l’annotation suffit.
}
