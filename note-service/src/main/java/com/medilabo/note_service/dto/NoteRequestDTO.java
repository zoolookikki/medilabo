package com.medilabo.note_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NoteRequestDTO {
    @NotNull(message = "Patient Id is mandatory")
    private Long patientId;

    @NotBlank(message = "Content is mandatory")
    private String content;
}
