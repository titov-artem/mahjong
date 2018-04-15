package com.github.mahjong.main.service.model;

import com.github.mahjong.main.model.PlayerScore;
import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class RoundScore {

    private final Map<Long, PlayerScore> playerIdToScore;
    private final Set<Long> winners;
    private final Set<Long> losers;
    // todo add penalties here

    public RoundScore(Map<Long, PlayerScore> playerIdToScore, Set<Long> winners, Set<Long> losers) {
        Preconditions.checkArgument(playerIdToScore.size() == 4, "Only 4 players can play mahjong");
        Preconditions.checkArgument(winners.size() <= 4, "Winner have to be not more than players");
        Preconditions.checkArgument(losers.size() <= 4, "Loosers have to be not more than players");
        Preconditions.checkArgument(playerIdToScore.keySet().containsAll(winners), "Winners have to be among players");
        Preconditions.checkArgument(playerIdToScore.keySet().containsAll(losers), "Losers have to be among players");
        this.playerIdToScore = playerIdToScore;
        this.winners = winners;
        this.losers = losers;
    }

    public Map<Long, PlayerScore> getPlayerIdToScore() {
        return Collections.unmodifiableMap(playerIdToScore);
    }

    public Set<Long> getWinners() {
        return Collections.unmodifiableSet(winners);
    }

    public Set<Long> getLosers() {
        return Collections.unmodifiableSet(losers);
    }

    public boolean isDraw() {
        return winners.isEmpty() && losers.isEmpty();
    }

    public boolean contains(Long playerId) {
        return playerIdToScore.containsKey(playerId);
    }
}
