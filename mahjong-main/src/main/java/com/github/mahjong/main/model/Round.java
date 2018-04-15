package com.github.mahjong.main.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Round {

    private String id;
    private long dealerId;
    private Wind wind;
    /**
     * Count of riichi sticks at the beginning of the round
     */
    private int riichiSticksCount;
    /**
     * Count of honba sticks at the beginning of the round
     */
    private int honbaSticksCount;

    // Maps from playerId to scores and raw scores descriptor
    private Map<Long, Integer> scores = new HashMap<>();
    private Map<Long, PlayerScore> rawScores = new HashMap<>();

    public static Round start(long dealerId, Wind wind) {
        return new Round(UUID.randomUUID().toString(), dealerId, wind, 0, 0, new HashMap<>(), new HashMap<>());
    }

    public static Round start(long dealerId, Wind wind, int riichiSticksCount, int honbaSticksCount) {
        return new Round(UUID.randomUUID().toString(), dealerId, wind, riichiSticksCount, honbaSticksCount, new HashMap<>(), new HashMap<>());
    }
}
