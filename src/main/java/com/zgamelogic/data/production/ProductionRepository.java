package com.zgamelogic.data.production;

import com.zgamelogic.data.enums.BuildingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductionRepository extends JpaRepository<Production, Production.CobbleProductionId> {
    List<Production> findAllById_Building(BuildingType id_building);
}
