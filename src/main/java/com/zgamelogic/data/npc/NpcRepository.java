package com.zgamelogic.data.npc;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NpcRepository extends JpaRepository<Npc, UUID> {
    List<Npc> findAllByPlayer_PlayerId(long userId);
    Optional<Npc> findByPlayer_PlayerIdAndId(long playerPlayerId, UUID id);
}
