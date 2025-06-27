package com.zgamelogic.data.history;

import com.zgamelogic.data.CobbleResourceConverter;
import com.zgamelogic.data.enums.CobbleResourceType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Map;

@Entity
@Table(name = "history_battle")
public class CobbleBattleHistory extends CobbleHistory {
    private boolean defending;
    private boolean won;
    @Convert(converter = CobbleResourceConverter.class)
    private Map<CobbleResourceType, Integer> resources;
    private int userPower;
    private int opponentPower;
    private long opponentId;
}
