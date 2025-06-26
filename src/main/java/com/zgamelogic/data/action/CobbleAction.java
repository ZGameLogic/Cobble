package com.zgamelogic.data.action;

import com.zgamelogic.data.building.CobbleBuilding;
import com.zgamelogic.data.enums.CobbleActionType;
import com.zgamelogic.data.player.CobblePlayer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Entity
@ToString
@Getter
@Table(name = "actions")
public class CobbleAction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Enumerated(EnumType.STRING)
    private CobbleActionType type;
    // Production remaining
    private int remaining;

    @OneToOne
    @JoinColumn(name = "cobbleBuildingId", referencedColumnName = "cobbleBuildingId", insertable = false, updatable = false)
    private CobbleBuilding building;

    @ManyToOne
    @JoinColumn(name = "playerId", referencedColumnName = "playerId", nullable = false)
    private CobblePlayer player;
}
