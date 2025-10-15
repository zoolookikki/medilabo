package com.medilabo.risk_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

// Enum Java en UPPER_SNAKE_CASE
// JSON en None | Borderline | InDanger | EarlyOnset
public enum RiskLevel {
    @JsonProperty("None")        NONE,
    @JsonProperty("Borderline")  BORDERLINE,
    @JsonProperty("InDanger")    IN_DANGER,
    @JsonProperty("EarlyOnset")  EARLY_ONSET
}

