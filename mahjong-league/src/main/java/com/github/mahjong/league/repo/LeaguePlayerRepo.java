package com.github.mahjong.league.repo;

import com.github.mahjong.league.model.LeaguePlayer;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LeaguePlayerRepo {

    Optional<LeaguePlayer> get(Long leagueId, Long playerId);

    List<LeaguePlayer> getAllByLeague(Long leagueId);

    List<LeaguePlayer> getAllByLeague(Long leagueId, Set<Long> playerIds);

    List<LeaguePlayer> getAllByPlayer(Long playerId);

    LeaguePlayer create(LeaguePlayer leaguePlayer);
}
