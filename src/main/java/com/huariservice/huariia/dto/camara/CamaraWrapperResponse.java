package com.huariservice.huariia.dto.camara;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collections;
import java.util.List;

/**
 * Generic response wrapper for paginated endpoints of Camara dos Deputados API.
 *
 * @param <T> payload item type
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CamaraWrapperResponse<T>(
        List<T> dados,
        List<CamaraLinkDto> links
) {
    public CamaraWrapperResponse {
        if (dados == null) {
            dados = Collections.emptyList();
        }
        if (links == null) {
            links = Collections.emptyList();
        }
    }
}
