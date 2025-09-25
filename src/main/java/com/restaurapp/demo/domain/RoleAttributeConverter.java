package com.restaurapp.demo.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleAttributeConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role attribute) {
        if (attribute == null) return null;
        return attribute.getValue(); // usa "admin", "mesero", etc.
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return Role.fromValue(dbData); // usa el método correcto del enum
    }
}
