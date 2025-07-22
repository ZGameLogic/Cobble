package com.zgamelogic.data.npc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zgamelogic.data.building.Building;
import com.zgamelogic.data.player.Player;
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
public class Npc {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private LocalDateTime born;
    private String firstName;
    private String lastName;
    private String appearance;

    @ManyToOne
    @JoinColumn(name = "buildingId", referencedColumnName = "buildingId")
    @JsonIgnore
    private Building building;

    @ManyToOne
    @JoinColumn(name = "playerId", referencedColumnName = "playerId", nullable = false)
    @JsonIgnore
    private Player player;

    public Npc(Player player, String firstname, String lastname, String appearance) {
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
        building = null;
    }
}
