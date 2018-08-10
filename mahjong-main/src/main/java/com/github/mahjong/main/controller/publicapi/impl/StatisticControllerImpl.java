package com.github.mahjong.main.controller.publicapi.impl;

import com.github.mahjong.common.exceptions.InternalInvariantFailedException;
import com.github.mahjong.main.model.Game;
import com.github.mahjong.main.model.GameData;
import com.github.mahjong.main.model.Player;
import com.github.mahjong.main.publicapi.StatisticController;
import com.github.mahjong.main.publicapi.dto.statistic.CombinationDistributionView;
import com.github.mahjong.main.publicapi.dto.statistic.PlaceDistributionView;
import com.github.mahjong.main.repo.GameRepo;
import com.github.mahjong.main.repo.PlayerRepo;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Controller
public class StatisticControllerImpl extends AbstractPlayerAwareController implements StatisticController {

    private final GameRepo gameRepo;

    @Inject
    public StatisticControllerImpl(PlayerRepo playerRepo, GameRepo gameRepo) {
        super(playerRepo);
        this.gameRepo = gameRepo;
    }

    @Override
    public PlaceDistributionView getPlaceDistribution(String rulesSetCode) {
        Player currentPlayer = getCurrentPlayer();
        List<Game> games = gameRepo.getAllByPlayer(currentPlayer);
        Map<Integer, Integer> placesCount = new HashMap<>();
        games.stream()
                .filter(Game::isCompleted)
                .filter(game -> game.getGameData().getRulesSetCode().equals(rulesSetCode))
                .forEach(game -> {
                    Integer place = game.getPlayerToPlace().get(game.getPlayerIds().indexOf(currentPlayer.getId()));
                    if (place == null) {
                        throw new InternalInvariantFailedException(String.format(
                                "Failed to get player's place. Player id %d, game id: %d",
                                currentPlayer.getId(), game.getId()
                        ));
                    }
                    Integer count = placesCount.putIfAbsent(place, 0);
                    placesCount.put(place, (count == null ? 0 : count) + 1);
                });
        return PlaceDistributionView.from(rulesSetCode, placesCount);
    }

    @Override
    public CombinationDistributionView getCombinationDistribution(String rulesSetCode) {
        Player currentPlayer = getCurrentPlayer();
        List<String> allGatheredCombinations = gameRepo.getAllByPlayer(currentPlayer).stream()
                .filter(game -> game.getGameData().getRulesSetCode().equals(rulesSetCode))
                .map(Game::getGameData)
                .flatMap(gameData -> gameData.getRounds().stream())
                .map(round -> round.getRawScores().get(currentPlayer.getId()))
                .flatMap(rawScore -> rawScore.getCombinationCodes().stream())
                .collect(toList());
        return CombinationDistributionView.from(rulesSetCode, allGatheredCombinations);
    }
}
