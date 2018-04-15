package com.github.mahjong.league.controller.impl;

import com.github.mahjong.league.exceptions.LeagueNotFoundException;
import com.github.mahjong.league.model.League;
import com.github.mahjong.league.repo.LeagueRepo;
import com.github.mahjong.league.service.PlayerCacheService;

import javax.ws.rs.NotFoundException;
import java.util.function.Supplier;

abstract class AbstractLeagueAwareController extends AbstractUserController {

    final LeagueRepo leagueRepo;

    AbstractLeagueAwareController(PlayerCacheService playerCacheService, LeagueRepo leagueRepo) {
        super(playerCacheService);
        this.leagueRepo = leagueRepo;
    }

    final League getLeagueInternal(Long id, boolean adminRequired) {
        // todo make thread local cache here
        League league = leagueRepo.get(id).orElseThrow(noSuchLeague(id));
        if (adminRequired && !league.getAdmins().contains(getCurrentPlayer().getId())) {
            // if current player is not admin of this league this league doesn't exist for him on this address
            throw noSuchLeague(id).get();
        }
        return league;
    }

    final Supplier<LeagueNotFoundException> noSuchLeague(Long id) {
        return LeagueNotFoundException.supplier(id);
    }
}
