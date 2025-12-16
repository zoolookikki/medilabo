package com.medilabo.risk_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "security.api")
@Validated
@Getter
@Setter
public class SecurityApiProperties {
    @NotBlank(message = "security.api.username must not be blank")
    private String username;

    @NotBlank(message = "security.api.password must not be blank")
    private String password;
}
