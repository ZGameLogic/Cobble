package com.zgamelogic.data.action;

import com.zgamelogic.data.building.Building;
import com.zgamelogic.data.enums.ActionType;
import com.zgamelogic.data.player.Player;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Entity
@ToString
@Getter
@Table(name = "actions")
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Enumerated(EnumType.STRING)
    private ActionType type;
    // Production remaining
    private int remaining;

    @OneToOne
    @JoinColumn(name = "buildingId", referencedColumnName = "buildingId", insertable = false, updatable = false)
    private Building building;

    @ManyToOne
    @JoinColumn(name = "playerId", referencedColumnName = "playerId", nullable = false)
    private Player player;
}
