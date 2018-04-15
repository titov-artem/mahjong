package com.github.mahjong.league.model;

import lombok.Data;

import java.util.List;

@Data
public class LeagueGame {

    private final Long leagueId;
    private final Long gameId;
    /**
     * Not ordered game players!
     */
    private final List<Long> playerIds;

}
