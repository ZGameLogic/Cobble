package com.zgamelogic.data.websocket;

import com.zgamelogic.data.enums.ResourceType;
import com.zgamelogic.data.player.Player;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class PlayerDTO {
    private final long playerId;
    private final LocalDateTime started;
    private final String townName;
    private final List<BuildingDTO> buildings;
    private final List<npcDTO> npcs;
    private final List<ActionDTO> actions;
    private final Map<ResourceType, Integer> storedResources;

    public PlayerDTO(Player player){
        playerId = player.getPlayerId();
        started = player.getStarted();
        townName = player.getTownName();
        buildings = player.getBuildings().stream().map(BuildingDTO::new).toList();
        npcs =  player.getNpcs().stream().map(npcDTO::new).toList();
        actions = player.getActions().stream().map(ActionDTO::new).toList();
        storedResources = player
            .getResources()
            .keySet()
            .stream()
            .filter(ResourceType::isStockpile)
            .collect(Collectors.toMap(
                key -> key,
                key -> player.getResources().get(key)
            ));
    }
}
