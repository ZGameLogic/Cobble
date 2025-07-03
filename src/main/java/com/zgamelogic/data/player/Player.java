package com.zgamelogic.data.player;

import com.zgamelogic.data.ResourceConverter;
import com.zgamelogic.data.CobbleServiceException;
import com.zgamelogic.data.action.Action;
import com.zgamelogic.data.building.Building;
import com.zgamelogic.data.enums.BuildingType;
import com.zgamelogic.data.enums.ResourceType;
import com.zgamelogic.data.npc.Npc;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "players")
public class Player {
    @Id
    private long playerId;
    private LocalDateTime started;
    @Setter
    private String townName;
    @Convert(converter = ResourceConverter.class)
    private Map<ResourceType, Integer> resources;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Building> buildings;
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Npc> npcs;
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Action> actions;

    public Player(long playerId, String name) {
        this.playerId = playerId;
        started = LocalDateTime.now();
        buildings = new ArrayList<>();
        npcs = new ArrayList<>();
        actions = new ArrayList<>();
        townName = name;
        resources = ResourceType.mapResources("", true);
    }

    public void addNpc(Npc npc) { npcs.add(npc); }

    public void addBuilding(BuildingType type, int level, String name, UUID buildingUUID) {
        Building building = new Building(this, type, level, name, buildingUUID);
        buildings.add(building);
    }

    public Npc getMayor() throws CobbleServiceException {
        return npcs.stream()
            .filter(npc -> npc.getBuilding().getType() == BuildingType.TOWN_HALL)
            .findFirst()
            .orElseThrow(() -> new CobbleServiceException("NPC not found"));
    }

    public void addResource(ResourceType type, int amount){
        resources.merge(type, amount, Integer::sum);
    }

    public void addResources(Map<ResourceType, Integer> resources){
        resources.forEach((k, v) -> resources.merge(k, v, Integer::sum));
    }

    public void removeResources(Map<ResourceType, Integer> resources){
        resources.forEach((k, v) -> resources.merge(k, -v, Integer::sum));
    }

    public boolean canAfford(Map<ResourceType, Integer> resources) {
        for(Map.Entry<ResourceType, Integer> entry : resources.entrySet()) {
            if(entry.getValue() > this.resources.get(entry.getKey())) return false;
        }
        return true;
    }

    public int populationCount(){ return npcs.size(); }
    public int populationCapacity(){ return buildings.stream().mapToInt(b -> b.getResource(ResourceType.POPULATION)).sum(); }
}
