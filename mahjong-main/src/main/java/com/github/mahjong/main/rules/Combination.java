package com.github.mahjong.main.rules;

/**
 * Combination represent single mahjong combination. There are can be only ine combination with same code in player's
 * hand.
 * <p>
 * All combinations from one {@link RulesSet} will be splitted on groups according to {@link #getGroup()}
 * and shown on UI starting from lowest group number to highest. Each group will be separated from another.
 * <p>
 * Inside single group combinations will be sorted according to {@link #getOrder()}
 */
public interface Combination {

    /**
     * Yakuman score
     */
    int SCORE_LIMIT = 1000;

    String getCode();

    int getScore(boolean isOpenHand);

    /**
     * @return group number to which combination belong to
     */
    default int getGroup() {
        return 0;
    }

    /**
     * @return combination order, which will be used to sort combinations inside the group
     */
    default int getOrder() {
        return 0;
    }

}
