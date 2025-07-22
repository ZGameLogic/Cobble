package com.zgamelogic.data.websocket;

import com.zgamelogic.data.npc.Npc;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class npcDTO {
    private final UUID id;
    private final LocalDateTime born;
    private final String firstName;
    private final String lastName;
    private final String appearance;
    private final UUID buildingId;

    public npcDTO(Npc npc) {
        this.id = npc.getId();
        born = npc.getBorn();
        firstName = npc.getFirstName();
        lastName = npc.getLastName();
        appearance = npc.getAppearance();
        buildingId = npc.getBuilding() != null ? npc.getBuilding().getBuildingId() : null;
    }
}
