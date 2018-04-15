package com.github.mahjong.main.service.model;

import com.github.mahjong.main.model.Wind;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.List;
import java.util.Map;

public class GameSeating {

    private final BiMap<Wind, Long> playerByWind;

    public GameSeating(Map<Wind, Long> playerByWind) {
        this.playerByWind = HashBiMap.create(playerByWind);
    }

    public GameSeating(List<Long> orderedPlayerIds) {
        this.playerByWind = HashBiMap.create();
        List<Wind> ordered = Wind.getOrdered();
        for (int i = 0; i < ordered.size(); i++) {
            this.playerByWind.put(ordered.get(i), orderedPlayerIds.get(i));
        }
    }

    public Long getPlayerOn(Wind wind) {
        return playerByWind.get(wind);
    }

    public Wind getWindFor(Long playerId) {
        return playerByWind.inverse().get(playerId);
    }

}
