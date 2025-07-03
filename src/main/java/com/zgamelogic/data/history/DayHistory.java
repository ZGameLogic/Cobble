package com.zgamelogic.data.history;

import com.zgamelogic.data.ResourceConverter;
import com.zgamelogic.data.enums.ResourceType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Map;

@Entity
@Table(name = "history_day")
public class DayHistory extends History {
    @Convert(converter = ResourceConverter.class)
    private Map<ResourceType, Integer> resourcesGained;
}
