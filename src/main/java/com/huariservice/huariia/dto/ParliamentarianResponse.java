package com.huariservice.huariia.dto;

import com.huariservice.huariia.entities.Parliamentarian;
import com.huariservice.huariia.entities.enums.ParliamentarianChamber;

public record ParliamentarianResponse(
        Long id,
        String name,
        ParliamentarianChamber chamber,
        String party,
        String state,
        String externalId
) {
    public static ParliamentarianResponse from(Parliamentarian entity) {
        return new ParliamentarianResponse(
                entity.getId(),
                entity.getName(),
                entity.getChamber(),
                entity.getParty(),
                entity.getState(),
                entity.getExternalId()
        );
    }
}
