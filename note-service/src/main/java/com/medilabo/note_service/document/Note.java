package com.medilabo.note_service.document;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document(collection = "notes")
@Getter 
@Setter 
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Note {
    // Définit le champ comme clé primaire (attention le type est String !).
    @Id
    private String id;

    // Référence vers l’id du patient (stocké dans la base SQL de Patient-service). 
    @Indexed
    private Long patientId;

    private String content;

    // Date de création (pour affichage chronologique) renseignée automatiquement par Spring Data :
    //  - En NOSQL on n'a pas de champ DEFAULT CURRENT_TIMESTAMP comme en MySQL.
    //  - plus portable (SQL + NoSQL) que @CreationTimestamp.
    @CreatedDate
    private Instant createdAt;
}
