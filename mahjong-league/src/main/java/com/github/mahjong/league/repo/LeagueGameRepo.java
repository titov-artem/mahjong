package com.github.mahjong.league.repo;

import com.github.mahjong.league.model.LeagueGame;

import java.util.List;

public interface LeagueGameRepo {

    LeagueGame create(LeagueGame leagueGame);

    List<LeagueGame> getAllWithPlayer(long leagueId, long playerId);
}
