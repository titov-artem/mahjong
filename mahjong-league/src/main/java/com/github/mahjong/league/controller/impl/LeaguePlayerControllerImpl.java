package com.github.mahjong.league.controller.impl;

import com.github.mahjong.api.common.dto.PlayerShortView;
import com.github.mahjong.league.controller.LeaguePlayerController;
import com.github.mahjong.league.controller.dto.PlayerShortViewHelper;
import com.github.mahjong.league.model.LeaguePlayer;
import com.github.mahjong.league.repo.LeaguePlayerRepo;
import com.github.mahjong.league.service.PlayerCacheService;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Controller("leaguePlayerController")
public class LeaguePlayerControllerImpl implements LeaguePlayerController {

    private final PlayerCacheService playerCacheService;
    private final LeaguePlayerRepo leaguePlayerRepo;

    @Inject
    public LeaguePlayerControllerImpl(PlayerCacheService playerCacheService,
                                      LeaguePlayerRepo leaguePlayerRepo) {
        this.playerCacheService = playerCacheService;
        this.leaguePlayerRepo = leaguePlayerRepo;
    }

    @Override
    public List<PlayerShortView> getAll(Long leagueId) {
        Set<Long> playerIds = leaguePlayerRepo.getAllByLeague(leagueId).stream()
                .map(LeaguePlayer::getPlayerId)
                .collect(toSet());
        return playerCacheService.getPlayersById(playerIds).stream()
                .map(PlayerShortViewHelper::from)
                .collect(toList());
    }
}
