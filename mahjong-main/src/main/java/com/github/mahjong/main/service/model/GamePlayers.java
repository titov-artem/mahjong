package com.github.mahjong.main.service.model;

import com.google.common.base.Preconditions;
import com.github.mahjong.main.model.Player;
import com.github.mahjong.main.model.Wind;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class GamePlayers {

    private final Map<Long, Player> players;
    private final Map<Wind, Player> windToPlayer;

    public GamePlayers(@Nonnull Map<Long, Player> players, @Nonnull Map<Wind, Player> windToPlayer) {
        Preconditions.checkArgument(players.size() == 4, "Only 4 players can play mahjong");
        windToPlayer.forEach((wind, player) ->
                Preconditions.checkArgument(players.containsKey(player.getId()),
                        "Unknown player on wind %s", wind)
        );
        this.players = players;
        this.windToPlayer = windToPlayer;
    }

    public Map<Long, Player> getPlayers() {
        return Collections.unmodifiableMap(players);
    }

    public Map<Wind, Player> getWindToPlayer() {
        return Collections.unmodifiableMap(windToPlayer);
    }
}
