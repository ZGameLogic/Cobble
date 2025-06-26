package com.zgamelogic.data.npc;

import com.zgamelogic.data.building.CobbleBuilding;
import com.zgamelogic.data.player.CobblePlayer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "npcs")
public class CobbleNpc {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private LocalDateTime born;
    private String firstName;
    private String lastName;
    private String appearance;

    @ManyToOne
    @JoinColumn(name = "cobbleBuildingId", referencedColumnName = "cobbleBuildingId")
    private CobbleBuilding cobbleBuilding;

    @ManyToOne
    @JoinColumn(name = "playerId", referencedColumnName = "playerId", nullable = false)
    private CobblePlayer player;

    public CobbleNpc(CobblePlayer player, String firstname, String lastname, String appearance) {
        this.firstName = firstname;
        this.lastName = lastname;
        this.appearance = appearance;
        this.player = player;
        born = LocalDateTime.now();
    }

    public String getFullName(){
        return firstName + " " + lastName;
    }

    public void fire(){
        cobbleBuilding = null;
    }
}
