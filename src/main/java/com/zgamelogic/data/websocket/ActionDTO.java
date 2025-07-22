package com.zgamelogic.data.websocket;

import com.zgamelogic.data.action.Action;
import com.zgamelogic.data.enums.ActionType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ActionDTO {
    private final UUID id;
    private final ActionType type;
    private final int remaining;
    private final UUID buildingId;

    public ActionDTO(Action action){
        id = action.getId();
        type = action.getType();
        remaining = action.getRemaining();
        buildingId = action.getBuilding().getBuildingId();
    }
}
