package com.github.mahjong.main.publicapi.dto;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class PlayerScoreDto {

    /**
     * Id of player to whom this score belongs to
     */
    public long playerId;
    /**
     * List of combination codes, that player collect in his hand
     */
    @NotNull
    public List<String> combinationCodes = new ArrayList<>();
    /**
     * Count of doras
     */
    public int doraCount;
    /**
     * Amount of fu points
     */
    public int fuCount;
    /**
     * True if player opened his hand
     */
    public boolean openHand;
    /**
     * True if player has riichi and game's rules set support riichi combination
     */
    public boolean riichi;
    /**
     * True if player DOESN'T win, but has a tempai at the end (if it make sense)
     */
    public boolean tempai;
}
