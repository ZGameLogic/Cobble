package com.zgamelogic.data.production;

import com.zgamelogic.data.BuildingConverter;
import com.zgamelogic.data.ResourceConverter;
import com.zgamelogic.data.enums.BuildingType;
import com.zgamelogic.data.enums.ResourceType;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Entity
@Getter
@ToString
@Table(name = "productions")
public class Production {
    @EmbeddedId
    private CobbleProductionId id;
    private int workers;

    // The cost it takes to upgrade to this level from the previous
    @Convert(converter = ResourceConverter.class)
    private Map<ResourceType, Integer> cost;
    // The amount of production per day this building produces
    @Convert(converter = ResourceConverter.class)
    private Map<ResourceType, Integer> production;
    // The amount of resources consumed per day for this building to produce
    @Convert(converter = ResourceConverter.class)
    private Map<ResourceType, Integer> consumption;
    // The buildings this level unlocks (not cumulative)
    @Convert(converter = BuildingConverter.class)
    private Map<BuildingType, Integer> unlocks;

    @Getter
    @ToString
    @Embeddable
    @EqualsAndHashCode
    public static class CobbleProductionId {
        @Enumerated(EnumType.STRING)
        private BuildingType building;
        private int level;
    }
}
