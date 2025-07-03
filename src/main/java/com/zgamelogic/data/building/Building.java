package com.zgamelogic.data.building;

import com.zgamelogic.data.enums.BuildingType;
import com.zgamelogic.data.enums.ResourceType;
import com.zgamelogic.data.npc.Npc;
import com.zgamelogic.data.player.Player;
import com.zgamelogic.data.production.Production;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@ToString
@NoArgsConstructor
@Table(name = "buildings")
public class Building {
    @Id
    private UUID buildingId;
    private int level;
    private LocalDateTime buildTime;
    @Enumerated(EnumType.STRING)
    private BuildingType type;
    @Setter
    private String buildingName;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "type", referencedColumnName = "building", insertable = false, updatable = false),
        @JoinColumn(name = "level", referencedColumnName = "level", insertable = false, updatable = false)
    })
    private Production production;

    @OneToMany
    @JoinColumn(name = "buildingId", referencedColumnName = "buildingId")
    private List<Npc> npcs;

    @ManyToOne
    @JoinColumn(name = "playerId", referencedColumnName = "playerId", nullable = false)
    private Player player;

    public Building(Player player, BuildingType type, int level, String name, UUID buildingId) {
        this.level = level;
        buildingName = name;
        this.type = type;
        this.player = player;
        this.buildingId = buildingId;
        buildTime= LocalDateTime.now();
    }

    public int getResource(ResourceType type){ return production.getProduction().getOrDefault(type, 0); }
}
