package com.medilabo.risk_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "medilabo")
@Validated
@Getter
@Setter
public class MedilaboApiProperties {
    @NotBlank(message = "medilabo.patient-url-api must not be blank")
    private String patientUrlApi;

    @NotBlank(message = "medilabo.note-url-api must not be blank")
    private String noteUrlApi;
}
