package com.huariservice.huariia.dto;

import com.huariservice.huariia.entities.enums.ParliamentarianChamber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ParliamentarianRequest(
        @NotBlank(message = "Name is required.")
        @Size(max = 150)
        String name,
        @NotNull(message = "Chamber is required.")
        ParliamentarianChamber chamber,
        @NotBlank(message = "Party is required.")
        @Size(max = 30)
        String party,
        @NotBlank(message = "State (UF) is required.")
        @Size(min = 2, max = 2)
        String state,
        @Size(max = 60)
        String externalId
) {}
