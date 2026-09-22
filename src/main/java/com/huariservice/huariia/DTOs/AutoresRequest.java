package com.huariservice.huariia.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;



public record AutoresRequest(
        @NotBlank @Size(max = 100) String nomeCanal,
        @NotBlank @Size(max = 254) String linkCanal
) {}
