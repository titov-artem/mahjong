package com.github.mahjong.main.rules;

import com.github.mahjong.common.enums.LangIso639;
import com.github.mahjong.main.model.Round;
import com.github.mahjong.main.service.model.GameSeating;
import com.github.mahjong.main.service.model.RoundScore;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RulesSet {

    String getCode();

    String getName(LangIso639 lang);

    String getDescription(LangIso639 lang);

    String getCombinationName(Combination combination, LangIso639 lang);

    List<Combination> getAvailableCombinations();

    default Optional<Combination> getRiichiCombination() {
        return Optional.empty();
    }

    void calculateRoundScore(RoundScore roundScore,
                             Round round,
                             GameSeating seating);

    /**
     * Will be called when last round of last wind was played.
     * Have to return is it possible to end game now, or last round have to be repeated,
     * or next wind should be started
     *
     * @param scoreByPlayer   score by player
     * @param dealerId        dealer
     * @param isDealerSucceed is dealer won in last round or had a tempai
     * @return
     */
    GameEndOptions canCompleteGame(Map<Long, Integer> scoreByPlayer, Long dealerId, boolean isDealerSucceed);

    enum GameEndOptions {
        END,
        REPEAT_ROUND,
        PLAY_NEXT_WIND
    }
}
