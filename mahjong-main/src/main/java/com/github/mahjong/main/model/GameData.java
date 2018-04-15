package com.github.mahjong.main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import lombok.*;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.*;

@NotThreadSafe
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameData {

    private String rulesSetCode;
    private int startPoints;
    private Set<Wind> windsToPlay;
    private boolean withUma;
    @Setter(AccessLevel.NONE)
    private List<Round> rounds;
    @Setter(AccessLevel.NONE)
    private List<Penalty> penalties;

    public static GameData newData(String rulesSetCode,
                                   int startPoints,
                                   Set<Wind> windsToPlay,
                                   boolean withUma) {
        Wind lastWind = null;
        for (Wind wind : windsToPlay) {
            if (!windsToPlay.contains(wind.next())) {
                Preconditions.checkArgument(lastWind == null, "Can play only sequential set of winds");
                lastWind = wind;
            }
        }
        return new GameData(rulesSetCode,
                startPoints,
                windsToPlay,
                withUma,
                new ArrayList<>(),
                new ArrayList<>());
    }

    /**
     * @return the wind which will be the last wind of the round in the game
     */
    @JsonIgnore
    public Wind getLastGameWind() {
        for (Wind wind : windsToPlay) {
            if (!windsToPlay.contains(wind.next())) {
                return wind;
            }
        }
        throw new IllegalStateException("No winds found!");
    }

    @JsonIgnore
    public Round getLastRound() {
        return rounds.get(rounds.size() - 1);
    }

}
