package com.medilabo.patient_service.integration;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;

import com.medilabo.patient_service.model.Patient;
import com.medilabo.patient_service.repository.PatientRepository;

@SpringBootTest
/* 
Base de données en mémoire pendant les tests (H2)
Spring Boot génère automatiquement le schéma à partir de ce qui est déclarés dans les entités, en utilisant Hibernate.
*/
@AutoConfigureTestDatabase
//Charge MockMvc avec SpringBootTest
@AutoConfigureMockMvc
// pour utiliser application-test.properties (écrase certaines valeurs de application.properties).
@ActiveProfiles("test")
public class PatientTestIT {
    
    // @Autowired pour Junit5, c'est plus simple.
    @Autowired 
    MockMvc mvc;
    @Autowired
    private PatientRepository patientRepository;

    private Long patient1Id;
    private Long patient2Id;
    
    @BeforeEach
    void setup() {
        // pour nettoyer la base à chaque fois.
        patientRepository.deleteAll();
        Patient patient1 = new Patient(null, "Doe", "John", LocalDate.of(1963, 1, 1), Patient.Gender.MALE, "1 Main Street", "123-456-7890");
        patient1Id = patientRepository.save(patient1).getId();
        Patient patient2 = new Patient(null, "Doe", "Jane", LocalDate.of(1963, 12, 31), Patient.Gender.FEMALE, "2 Main Street", "098-765-4321");
        patient2Id = patientRepository.save(patient2).getId();
    }

    @Test
    void getAllPatients() throws Exception {
        mvc.perform(get("/patients"))
           .andExpect(status().isOk())
           .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$").isArray())
           .andExpect(jsonPath("$.length()").value(2))
           .andExpect(jsonPath("$[0].lastName").value("Doe"))
           .andExpect(jsonPath("$[0].firstName").value("John"))
           .andExpect(jsonPath("$[1].lastName").value("Doe"))
           .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }    
    
    @Test
    void getPatientsByIdFound() throws Exception {
        mvc.perform(get("/patients/{id}", patient1Id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(patient1Id));
    }    
    
    @Test
    void getPatientsByIdNotFound() throws Exception {
        mvc.perform(get("/patients/{id}", 99))
        .andExpect(status().isNotFound());
    }    

    @Test
    void getPatientsByIdNotNumeric() throws Exception {
        mvc.perform(get("/patients/xxx"))
        .andExpect(status().isBadRequest());
    }    
    
    @Test
    void postPatientsSuccessfull() throws Exception {
        String jsonBody = """
        {
          "lastName": "xxx",
          "firstName": "xxx",
          "birthDate": "1900-01-01",
          "gender": "UNKNOW",
          "address": "xxx",
          "phoneNumber": "000-000-0000"
        }
        """;
        
        mvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
           .andExpect(status().isCreated())
           .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.id").isNumber())
           .andExpect(jsonPath("$.lastName").value("xxx"))
           .andExpect(jsonPath("$.firstName").value("xxx"));
    }

    @Test
    void postPatientsUnSuccessfullMandatory() throws Exception {
        String jsonBody = """
        {
        }
        """;
        
        mvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.lastName").value(org.hamcrest.Matchers.containsString("mandatory")))
            .andExpect(jsonPath("$.firstName").value(org.hamcrest.Matchers.containsString("mandatory")))
            .andExpect(jsonPath("$.birthDate").value(org.hamcrest.Matchers.containsString("mandatory")))
            .andExpect(jsonPath("$.gender").value(org.hamcrest.Matchers.containsString("mandatory")));
    }

    @Test
    void postPatientsInvalidDateInThePastAndPhoneNumber() throws Exception {
        String jsonBody = """
        {
          "lastName": "xxx",
          "firstName": "xxx",
          "phoneNumber": "000",
          "birthDate": "2900-01-01",
          "gender": "MALE"
        }
        """;
        
        mvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.phoneNumber").value("Phone number must be in format XXX-XXX-XXXX"))
            .andExpect(jsonPath("$.birthDate").value("Birth date must be in the past"));
    }
    
    @Test
    void postPatientsInvalidJacksonControl() throws Exception {
        String jsonBody = """
        {
          "lastName": "xxx",
          "firstName": "xxx",
          "birthDate": "1900-01-01",
          "gender": "XXX"
        }
        """;
        
        mvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.gender").value(org.hamcrest.Matchers.containsString("Invalid")));
        
        jsonBody = """
        {
          "lastName": "xxx",
          "firstName": "xxx",
          "birthDate": "1900-01-32",
          "gender": "XXX",
        }
        """;
        
        mvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$['birthDate']").value(org.hamcrest.Matchers.containsString("Invalid")));
    }

    @Test
    void putPatientsUpdateOKAndNotFound() throws Exception {
        String jsonBody = """
        {
          "lastName": "Done",
          "firstName": "Jess",
          "birthDate": "2000-01-01",
          "gender": "MALE",
          "address": "yyy",
          "phoneNumber": "999-999-9999"
        }
        """;
        
        mvc.perform(put("/patients/{id}", patient2Id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(patient2Id))
           .andExpect(jsonPath("$.lastName").value("Done"))
           .andExpect(jsonPath("$.firstName").value("Jess"))
           .andExpect(jsonPath("$.birthDate").value("2000-01-01"))
           .andExpect(jsonPath("$.gender").value("MALE"))
           .andExpect(jsonPath("$.address").value("yyy"))
           .andExpect(jsonPath("$.phoneNumber").value("999-999-9999"));
        
        mvc.perform(put("/patients/{id}", 99)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
           .andExpect(status().isNotFound());
    }

    
    @Test
    void getBadPath() throws Exception {
        mvc.perform(get("/patient"))
           .andExpect(status().isNotFound());
    }    
    
    @Test
    void getSimulateInternalError() throws Exception {
        mvc.perform(get("/patients/simulate500"))
           .andExpect(status().isInternalServerError());
    }    
}
