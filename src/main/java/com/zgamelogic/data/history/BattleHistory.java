package com.zgamelogic.data.history;

import com.zgamelogic.data.ResourceConverter;
import com.zgamelogic.data.enums.ResourceType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Map;

@Entity
@Table(name = "history_battle")
public class BattleHistory extends History {
    private boolean defending;
    private boolean won;
    @Convert(converter = ResourceConverter.class)
    private Map<ResourceType, Integer> resources;
    private int userPower;
    private int opponentPower;
    private long opponentId;
}
