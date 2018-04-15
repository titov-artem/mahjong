package com.github.mahjong.main.controller.dto;

import com.github.mahjong.api.common.dto.GameView;
import com.github.mahjong.api.common.dto.WindDto;
import com.github.mahjong.main.model.*;
import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toSet;

public class GameViewHelper {

    public static GameView from(Game game, Map<Long, Player> players) {
        GameView view = new GameView();
        view.id = game.getId();
        view.rulesSetCode = game.getGameData().getRulesSetCode();

        List<Wind> winds = Wind.getOrdered();
        List<Integer> scores = game.getCurrentScore();
        Round currentRound = game.getGameData().getLastRound();
        int dealerIndex = game.getPlayerIds().indexOf(currentRound.getDealerId());
        view.players = new HashMap<>();
        view.scores = new HashMap<>();
        view.windMapping = new HashMap<>();
        for (int i = 0; i < winds.size(); i++) {
            Long playerId = game.getPlayerIds().get(i);
            Player player = players.get(playerId);
            Preconditions.checkArgument(player != null, "Player with id %s missed", playerId);

            WindDto windDto = WindDtoHelper.from(winds.get(i));

            view.players.put(windDto, PlayerShortViewHelper.from(player));
            view.scores.put(windDto, scores.get(i));
            view.windMapping.put(windDto, WindDtoHelper.from(winds.get((i + winds.size() - dealerIndex) % winds.size())));
        }

        view.riichiSticksOnTable = currentRound.getRiichiSticksCount();
        view.honbaCount = currentRound.getHonbaSticksCount();
        view.currentDealer = currentRound.getDealerId();
        view.currentWind = WindDtoHelper.from(currentRound.getWind());
        view.currentWindRoundNumber = dealerIndex + 1;

        view.windsToPlay = game.getGameData().getWindsToPlay().stream().map(WindDtoHelper::from).collect(toSet());
        view.startPoints = game.getGameData().getStartPoints();
        view.withUma = game.getGameData().isWithUma();

        view.completed = game.isCompleted();

        return view;
    }

}
