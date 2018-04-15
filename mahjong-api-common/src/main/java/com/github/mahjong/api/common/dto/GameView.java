package com.github.mahjong.api.common.dto;

import java.util.Map;
import java.util.Set;

public class GameView {

    public long id;
    public String rulesSetCode;
    /**
     * Map from initial wind to player. (Not from current player wind)
     */
    public Map<WindDto, PlayerShortView> players;
    /**
     * Map from initial wind to player's score. (Not from current player wind)
     */
    public Map<WindDto, Integer> scores;
    /**
     * Map from initial wind to current player's wind.
     */
    public Map<WindDto, WindDto> windMapping;
    public int riichiSticksOnTable;
    public int honbaCount;
    public long currentDealer;
    public WindDto currentWind;
    public int currentWindRoundNumber;

    public Set<WindDto> windsToPlay;
    public int startPoints;
    public boolean withUma;

    public boolean completed;

}
