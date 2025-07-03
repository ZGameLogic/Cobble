package com.zgamelogic.data.history;

import com.zgamelogic.data.enums.ActionType;
import com.zgamelogic.data.enums.BuildingType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "history_build")
public class BuildHistory extends History {
    private int level;
    @Enumerated(EnumType.STRING)
    private ActionType action;
    @Enumerated(EnumType.STRING)
    private BuildingType buildingType;
    private UUID buildingId;
}
