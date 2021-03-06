package com.github.mahjong.main.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PlayerScore {

    /**
     * Empty list means none.
     */
    private final List<String> combinationCodes;
    private final int doraCount;
    private final int fuCount;
    private final boolean openHand;
    /**
     * True is player declared riichi in this round.
     */
    private final boolean riichi;
    /**
     * True is player tempai in the end of the round with draw.
     */
    private final boolean tempai;

    public PlayerScore(@JsonProperty("combinationCodes") List<String> combinationCodes,
                       @JsonProperty("doraCount") int doraCount,
                       @JsonProperty("fuCount") int fuCount,
                       @JsonProperty("openHand") boolean openHand,
                       @JsonProperty("riichi") boolean riichi,
                       @JsonProperty("tempai") boolean tempai) {
        this.combinationCodes = combinationCodes;
        this.doraCount = doraCount;
        this.fuCount = fuCount;
        this.openHand = openHand;
        this.riichi = riichi;
        this.tempai = tempai;
    }
}
