package com.zgamelogic.data.history;

import com.zgamelogic.data.enums.CobbleActionType;
import com.zgamelogic.data.enums.CobbleBuildingType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "history_build")
public class CobbleBuildHistory extends CobbleHistory {
    private int level;
    @Enumerated(EnumType.STRING)
    private CobbleActionType action;
    @Enumerated(EnumType.STRING)
    private CobbleBuildingType buildingType;
    private UUID buildingId;
}
