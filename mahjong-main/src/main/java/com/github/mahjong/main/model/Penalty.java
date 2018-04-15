package com.github.mahjong.main.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Penalty {

    private long playerId;
    private String roundId;
    private Type type;
    private int amount;

    public enum Type {
        CHOMBO, DEAD_HAND
    }

}
