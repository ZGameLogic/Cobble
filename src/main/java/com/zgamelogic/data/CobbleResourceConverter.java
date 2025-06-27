package com.zgamelogic.data;

import com.zgamelogic.data.enums.CobbleResourceType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

@Converter
public class CobbleResourceConverter implements AttributeConverter<Map<CobbleResourceType, Integer>, String> {
    @Override
    public String convertToDatabaseColumn(Map<CobbleResourceType, Integer> attribute) {
        return CobbleResourceType.mapResources(attribute);
    }

    @Override
    public Map<CobbleResourceType, Integer> convertToEntityAttribute(String dbData) {
        return CobbleResourceType.mapResources(dbData, true);
    }
}
