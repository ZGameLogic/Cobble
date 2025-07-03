package com.zgamelogic.data;

import com.zgamelogic.data.enums.ResourceType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

@Converter
public class ResourceConverter implements AttributeConverter<Map<ResourceType, Integer>, String> {
    @Override
    public String convertToDatabaseColumn(Map<ResourceType, Integer> attribute) {
        return ResourceType.mapResources(attribute);
    }

    @Override
    public Map<ResourceType, Integer> convertToEntityAttribute(String dbData) {
        return ResourceType.mapResources(dbData, true);
    }
}
