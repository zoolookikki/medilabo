package com.medilabo.webapp.config;

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
    @NotBlank(message = "medilabo.url-api must not be blank")
    private String urlApi;
}
