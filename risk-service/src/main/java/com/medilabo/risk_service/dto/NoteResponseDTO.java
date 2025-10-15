package com.medilabo.risk_service.dto;

import java.time.Instant;

import lombok.Data;

@Data
public class NoteResponseDTO {
    private String id;

    private Long patientId;
    
    private String content;
    
    private Instant createdAt;
}
