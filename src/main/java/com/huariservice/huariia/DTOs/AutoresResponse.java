package com.huariservice.huariia.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutoresResponse {

    private Long id;
    private String nomeCanal;
    private String linkCanal;
}
