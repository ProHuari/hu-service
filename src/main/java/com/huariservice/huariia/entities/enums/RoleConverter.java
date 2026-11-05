package com.huariservice.huariia.entities.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Locale;

/**
 * JPA attribute converter for {@link Role} that preserves compatibility with legacy
 * Portuguese database values ({@code ALUNO} -> {@link Role#STUDENT}).
 */
@Converter
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role attribute) {
        return attribute != null ? attribute.name() : Role.STUDENT.name();
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Role.STUDENT;
        }
        String normalized = dbData.trim().toUpperCase(Locale.ROOT);
        if ("ALUNO".equals(normalized) || "STUDENT".equals(normalized)) {
            return Role.STUDENT;
        }
        return Role.valueOf(normalized);
    }
}
