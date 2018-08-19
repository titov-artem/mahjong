package com.github.mahjong.main.rules;

import com.github.mahjong.common.enums.LangIso639;
import com.github.mahjong.main.model.Game;
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

    /**
     * @return if rules support riichi, returns combination for riichi, otherwise returns empty
     */
    default Optional<Combination> getRiichiCombination() {
        return Optional.empty();
    }

    /**
     * Calculate score specified round basing on rules score evaluation algorithm.
     *
     * @param roundScore received score for this round
     * @param round      round to update
     * @param seating    players seating
     * @return map from player to his score for this round (positive, 0 or negative)
     */
    Map<Long, Integer> calculateRoundScore(RoundScore roundScore,
                                           Round round,
                                           GameSeating seating);

    /**
     * Calculate final score for the game
     *
     * @param game              game
     * @param riichiSticksCount count of riichi sticks left on the table
     * @return map from player to his score with all changes applied, including uma and penalties,
     * if applicable
     */
    Map<Long, Integer> calculateFinalScore(Game game,
                                           int riichiSticksCount);

    /**
     * Will be called when last round of last wind was played.
     * Have to return is it possible to end game now, or last round have to be repeated,
     * or next wind should be started
     *
     * @param game            current game with last round completed
     * @param isDealerSucceed is dealer won in last round or had a tempai
     * @return action, that have to be made
     */
    GameEndOptions shouldCompleteGame(Game game,
                                      boolean isDealerSucceed);

    enum GameEndOptions {
        /**
         * Continue game with usual rules
         */
        CONTINUE,
        /**
         * Play next wind, even if it was last round of the last wind. This option makes
         * sense only for last rounds in the wind, otherwise use {@link #CONTINUE}
         */
        PLAY_NEXT_WIND,
        /**
         * End game right now
         */
        END
    }
}
