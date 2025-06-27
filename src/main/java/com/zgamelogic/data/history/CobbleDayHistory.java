package com.zgamelogic.data.history;

import com.zgamelogic.data.CobbleResourceConverter;
import com.zgamelogic.data.enums.CobbleResourceType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Map;

@Entity
@Table(name = "history_day")
public class CobbleDayHistory extends CobbleHistory {
    @Convert(converter = CobbleResourceConverter.class)
    private Map<CobbleResourceType, Integer> resourcesGained;
}
