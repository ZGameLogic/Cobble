package com.zgamelogic.data.history;

import com.zgamelogic.data.player.Player;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NoArgsConstructor
public abstract class History {
    @Id
    @GeneratedValue
    private UUID id;
    private LocalDateTime completed;

    @ManyToOne
    @JoinColumn(name = "playerId", referencedColumnName = "playerId", nullable = false)
    private Player player;
}
