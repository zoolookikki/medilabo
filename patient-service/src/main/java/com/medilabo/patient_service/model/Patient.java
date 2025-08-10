package com.medilabo.patient_service.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "patients")
@Getter 
@Setter 
@NoArgsConstructor
@ToString
public class Patient {
  public enum Gender { MALE, FEMALE, UNKNOW }

  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false, length=50)  
  private String lastName;

  @Column(nullable=false, length=50)  
  private String firstName;
  
  @Column(nullable=false)     
  //uniquement année, mois, jour
  private LocalDate birthDate;

  // Stocke le nom exact de l’élément.
  @Enumerated(EnumType.STRING)
  @Column(nullable=false, length=10)
  private Gender gender;

  @Column(length=255)
  private String address;
  
  @Column(length=12)
  private String phoneNumber;
}
