package com.zgamelogic.data.building;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BuildingRepository extends JpaRepository<Building, UUID> {
    Optional<Building> findByPlayer_PlayerIdAndBuildingId(long playerId, UUID buildingId);
    List<Building> findAllByPlayer_PlayerId(long playerPlayerId);
}
