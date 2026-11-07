package com.huariservice.huariia.dto.camara;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Representation of hypermedia link returned by Camara dos Deputados API pagination.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CamaraLinkDto(
        String rel,
        String href
) {}
