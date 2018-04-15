package com.github.mahjong.main.controller.privateapi.impl;

import com.github.mahjong.api.common.dto.GameView;
import com.github.mahjong.main.controller.dto.GameViewHelper;
import com.github.mahjong.main.controller.dto.WindDtoHelper;
import com.github.mahjong.main.model.Game;
import com.github.mahjong.main.model.Player;
import com.github.mahjong.main.model.Wind;
import com.github.mahjong.main.privateapi.GameController;
import com.github.mahjong.main.privateapi.dto.GameStartForm;
import com.github.mahjong.main.repo.GameRepo;
import com.github.mahjong.main.repo.PlayerRepo;
import com.github.mahjong.main.rules.RulesSet;
import com.github.mahjong.main.rules.RulesSetRegistry;
import com.github.mahjong.main.service.GameService;
import com.github.mahjong.main.service.model.GamePlayers;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

@Controller("privateGameController")
public class GameControllerImpl implements GameController {

    private final GameService gameService;
    private final RulesSetRegistry rulesSetRegistry;
    private final GameRepo gameRepo;
    private final PlayerRepo playerRepo;

    @Inject
    public GameControllerImpl(GameService gameService,
                              RulesSetRegistry rulesSetRegistry,
                              GameRepo gameRepo,
                              PlayerRepo playerRepo) {
        this.gameService = gameService;
        this.rulesSetRegistry = rulesSetRegistry;
        this.gameRepo = gameRepo;
        this.playerRepo = playerRepo;
    }

    @Override
    public GameView startGame(GameStartForm form) {
        Map<Long, Player> players = playerRepo.getAll(form.players).stream()
                .collect(toMap(Player::getId, identity()));
        Map<Wind, Player> windToPlayer = new HashMap<>();
        form.windToPlayer.forEach((windDto, playerId) -> {
            Wind wind = WindDtoHelper.toWind(windDto);
            Player player = players.get(playerId);
            if (player == null) {
                throw new NotFoundException("No player with id=" + playerId);
            }
            windToPlayer.put(wind, player);
        });

        RulesSet rulesSet = rulesSetRegistry.getRulesSet(form.rulesSet)
                .orElseThrow(() -> new NotFoundException("No rules set found"));

        Game game = gameService.startGame(
                new GamePlayers(players, windToPlayer),
                rulesSet,
                form.startPoints,
                form.windsToPlay.stream().map(WindDtoHelper::toWind).collect(toSet()),
                form.withUma
        );
        return GameViewHelper.from(game, players);
    }

    @Override
    public List<GameView> getAll(Set<Long> gameIds) {
        List<Game> games = gameRepo.getAll(gameIds);
        Set<Long> playerIds = games.stream()
                .map(Game::getPlayerIds)
                .flatMap(List::stream)
                .collect(toSet());
        Map<Long, Player> players = playerRepo.getAll(playerIds).stream()
                .collect(toMap(Player::getId, identity()));
        return games.stream()
                .map(game -> GameViewHelper.from(game, players))
                .collect(toList());
    }
}
