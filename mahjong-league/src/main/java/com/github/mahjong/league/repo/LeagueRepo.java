package com.github.mahjong.league.repo;

import com.github.mahjong.league.model.League;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LeagueRepo {

    List<League> getAll();

    List<League> getAll(Collection<Long> ids);

    List<League> getAllWithPlayer(Long playerId);

    List<League> getAllWithAdmin(Long adminId);

    Optional<League> get(Long id);

    League create(League league);

    void addAdmins(Long leagueId, Set<Long> admins);

    void removeAdmins(Long leagueId, Set<Long> admins);
}
