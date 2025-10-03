package com.medilabo.note_service.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.medilabo.note_service.document.Note;
import com.medilabo.note_service.repository.NoteRepository;

@SpringBootTest
//Charge MockMvc avec SpringBootTest
@AutoConfigureMockMvc
//pour utiliser application-test.properties (écrase certaines valeurs de application.properties).
@ActiveProfiles("test")
class NoteTestIT {
    // @Autowired pour Junit5, c'est plus simple.
    @Autowired MockMvc mvc;
    @Autowired NoteRepository noteRepository;
    
    private String note1Id;
    
    @BeforeEach
    void setup() {
        // pour nettoyer la base à chaque fois.
        noteRepository.deleteAll();
        Note note1 = new Note(null, 1L, "Le patient déclare qu'il 'se sent très bien'.\nPoids égal ou inférieur au poids recommandé.", null);
        Note note2 = new Note(null, 2L, "Le patient déclare qu'il ressent beaucoup de stress au travail.\nIl se plaint également que son audition est anormale dernièrement.", null);
        Note note3 = new Note(null, 2L, "Le patient déclare avoir fait une réaction aux médicaments au cours des 3 derniers mois.\\nIl remarque également que son audition continue d'être anormale.", null);
        note1Id = noteRepository.save(note1).getId();
        noteRepository.save(note2);
        noteRepository.save(note3);
    }

    @Test
    void geNotesByIdFound() throws Exception {
        mvc.perform(get("/notes/{id}", note1Id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(note1Id))
            .andExpect(jsonPath("$.patientId").value(1));
    }

    @Test
    void getNotesByIdNotFound() throws Exception {
        mvc.perform(get("/notes/{id}", "not exist"))
            .andExpect(status().isNotFound());
    }
    
    @Test
    void getNotesByPatientIdNotNumeric() throws Exception {
        mvc.perform(get("/notes/patient/{patientId}", "xxx"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getNotesByPatientIdReturn200WithContent() throws Exception {
        mvc.perform(get("/notes/patient/{patientId}", 2))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].patientId").value(2))
            .andExpect(jsonPath("$[1].patientId").value(2));
    }

    @Test
    void getNotesByPatientIdReturn200WithEmptyList() throws Exception {
        mvc.perform(get("/notes/patient/{patientId}", 99))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void postNotesSuccessfull() throws Exception {
        String jsonBody = """
          {
            "patientId": 3,
            "content": "Le patient déclare qu'il fume depuis peu."
          }
          """;

        mvc.perform(post("/notes")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$.id").isString())
            .andExpect(jsonPath("$.patientId").value(3))
            .andExpect(jsonPath("$.content").value("Le patient déclare qu'il fume depuis peu."));
    }
    
    @Test
    void postNotesUnSuccessfullMandatory() throws Exception {
        String jsonBody = """
        {
        }
        """;

        mvc.perform(post("/notes")
                .contentType("application/json")
                .content(jsonBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.patientId").value(org.hamcrest.Matchers.containsString("mandatory")))
            .andExpect(jsonPath("$.content").value(org.hamcrest.Matchers.containsString("mandatory")));
    }
    
    @Test
    void getBadPath() throws Exception {
        mvc.perform(get("/note/{id}", note1Id))
            .andExpect(status().isNotFound());
    }    
    
    @Test
    void getSimulateInternalError() throws Exception {
        mvc.perform(get("/notes/simulate500"))
            .andExpect(status().isInternalServerError());
    }
}
