package com.medilabo.webapp.client;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.medilabo.webapp.dto.PatientDTO;
import com.medilabo.webapp.exception.PatientServiceUnavailableException;

import lombok.RequiredArgsConstructor;

@Component
//Coupler @RequiredArgsConstructor avec des champs final pour rendre les dépendances immuables ==> mieux que @Autowired devenu obsolète.
@RequiredArgsConstructor
public class PatientClient {

  private final RestClient rest;

  public List<PatientDTO> findAll() {
      try {
        return rest
            // prépare une requête get.    
            .get()
            .uri("/patients")
            // exécute la requête.
            .retrieve()
            // désérialise la réponse JSON en List<PatientDto>.
            .body(new ParameterizedTypeReference<List<PatientDTO>>() {});
      // attention ici, bien faire la distinction sinon on ne sait pas si c'est la gateway ou le microservice Patient qui n'a pas répondu.
//      } catch (Exception e) {
      } catch (org.springframework.web.client.RestClientResponseException e) {        
          // Si on arrive ici, ça veut dire que la gateway a répondu (sinon ce serait une ConnectException). Le service patient est indisponible.
          throw new PatientServiceUnavailableException("Patient service unavailable", e);
      }
  }
}