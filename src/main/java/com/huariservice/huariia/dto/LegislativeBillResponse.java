package com.huariservice.huariia.dto;

import com.huariservice.huariia.entities.LegislativeBill;

public record LegislativeBillResponse(
        Long id,
        String code,
        String title,
        String summary,
        String fullText,
        String status,
        Long authorId,
        String authorName
) {
    public static LegislativeBillResponse from(LegislativeBill entity) {
        return new LegislativeBillResponse(
                entity.getId(),
                entity.getCode(),
                entity.getTitle(),
                entity.getSummary(),
                entity.getFullText(),
                entity.getStatus(),
                entity.getAuthor() != null ? entity.getAuthor().getId() : null,
                entity.getAuthor() != null ? entity.getAuthor().getName() : null
        );
    }
}

