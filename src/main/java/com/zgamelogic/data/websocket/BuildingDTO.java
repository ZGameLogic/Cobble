package com.zgamelogic.data.websocket;

import com.zgamelogic.data.building.Building;
import com.zgamelogic.data.enums.BuildingType;
import com.zgamelogic.data.npc.Npc;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class BuildingDTO {
    private final UUID id;
    private final int level;
    private final LocalDateTime constructed;
    private final BuildingType type;
    private final String name;
    private final Integer resourcePriority;
    private final List<UUID> workerIds;

    public BuildingDTO(Building building) {
        id = building.getBuildingId();
        level = building.getLevel();
        constructed = building.getBuildTime();
        type = building.getType();
        name = building.getBuildingName();
        resourcePriority = building.getPriority();
        workerIds = building.getNpcs().stream().map(Npc::getId).toList();
    }
}
