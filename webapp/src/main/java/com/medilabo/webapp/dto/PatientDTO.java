package com.medilabo.webapp.dto;


import java.time.LocalDate;

import lombok.Data;

@Data
public class PatientDTO {
  private Long id;
  private String lastName;
  private String firstName;
  private LocalDate birthDate;
  private String gender;       
  private String address;
  private String phoneNumber;
}
