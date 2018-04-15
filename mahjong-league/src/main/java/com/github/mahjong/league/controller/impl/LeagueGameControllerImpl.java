package com.github.mahjong.league.controller.impl;

import com.github.mahjong.api.common.dto.GameView;
import com.github.mahjong.common.exceptions.BadRequest;
import com.github.mahjong.league.controller.LeagueGameController;
import com.github.mahjong.league.model.League;
import com.github.mahjong.league.model.LeagueGame;
import com.github.mahjong.league.model.LeaguePlayer;
import com.github.mahjong.league.repo.LeagueGameRepo;
import com.github.mahjong.league.repo.LeaguePlayerRepo;
import com.github.mahjong.league.repo.LeagueRepo;
import com.github.mahjong.league.service.PlayerCacheService;
import com.github.mahjong.league.service.model.Player;
import com.github.mahjong.main.privateapi.GameController;
import com.github.mahjong.main.privateapi.dto.GameStartForm;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Controller("leagueGameContoller")
public class LeagueGameControllerImpl extends AbstractLeagueAwareController implements LeagueGameController {

    private final GameController gameController;
    private final LeaguePlayerRepo leaguePlayerRepo;
    private final LeagueGameRepo leagueGameRepo;

    @Inject
    public LeagueGameControllerImpl(PlayerCacheService playerCacheService,
                                    LeagueRepo leagueRepo,
                                    GameController gameController,
                                    LeaguePlayerRepo leaguePlayerRepo,
                                    LeagueGameRepo leagueGameRepo) {
        super(playerCacheService, leagueRepo);
        this.gameController = gameController;
        this.leaguePlayerRepo = leaguePlayerRepo;
        this.leagueGameRepo = leagueGameRepo;
    }

    @Override
    public List<GameView> getAll(Long leagueId) {
        League league = getLeagueInternal(leagueId, false);
        Player currentPlayer = getCurrentPlayer();
        //noinspection ResultOfMethodCallIgnored: Check that player belongs to league
        leaguePlayerRepo.get(league.getId(), currentPlayer.getId())
                .orElseThrow(noSuchLeague(league.getId()));
        Set<Long> gameIds = leagueGameRepo.getAllWithPlayer(league.getId(), currentPlayer.getId()).stream()
                .map(LeagueGame::getGameId)
                .collect(toSet());
        return gameController.getAll(gameIds);
    }

    @Override
    public List<GameView> getActive(@NotNull Long leagueId) {
        // todo implement me
        return Collections.emptyList();
    }

    @Override
    public GameView start(Long leagueId, GameStartForm form) {
        // todo add tests and fix bug: can start game with players not in league
        Player currentPlayer = getCurrentPlayer();
        Set<Long> playerIds = new HashSet<>(form.players);
        playerIds.add(currentPlayer.getId());
        Set<Long> leaguePlayerIds = leaguePlayerRepo.getAllByLeague(leagueId, playerIds).stream()
                .map(LeaguePlayer::getPlayerId)
                .collect(toSet());
        if (!leaguePlayerIds.contains(currentPlayer.getId())) {
            throw noSuchLeague(leagueId).get();
        }
        if (!leaguePlayerIds.containsAll(playerIds)) {
            // todo move code to enum
            throw new BadRequest("league.player.has.to.belong.to.league");
        }
        GameView gameView = gameController.startGame(form);
        leagueGameRepo.create(new LeagueGame(leagueId, gameView.id, form.players));
        return gameView;
    }
}
