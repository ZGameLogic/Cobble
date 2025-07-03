package com.zgamelogic.data;

import com.zgamelogic.data.enums.BuildingType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

@Converter
public class BuildingConverter implements AttributeConverter<Map<BuildingType, Integer>, String> {
    @Override
    public String convertToDatabaseColumn(Map<BuildingType, Integer> attribute) {
        return BuildingType.mapBuildings(attribute);
    }

    @Override
    public Map<BuildingType, Integer> convertToEntityAttribute(String dbData) {
        return BuildingType.mapBuildings(dbData, true);
    }
}
