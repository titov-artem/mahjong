package com.github.mahjong.main.service;

import com.github.mahjong.main.model.*;
import com.github.mahjong.main.repo.GameRepo;
import com.github.mahjong.main.rules.RulesSet;
import com.github.mahjong.main.rules.RulesSetRegistry;
import com.github.mahjong.main.service.model.GamePlayers;
import com.github.mahjong.main.service.model.GameSeating;
import com.github.mahjong.main.service.model.RoundScore;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
public class GameService {

    private final RulesSetRegistry rulesSetRegistry;
    private final GameRepo gameRepo;

    @Inject
    public GameService(RulesSetRegistry rulesSetRegistry, GameRepo gameRepo) {
        this.rulesSetRegistry = rulesSetRegistry;
        this.gameRepo = gameRepo;
    }

    /**
     * Create game with specified players
     *
     * @param gamePlayers players in the game
     * @param startPoints start points for players
     * @param windsToPlay winds to play, must be sequential list of winds
     * @param withUma     is it necessary to add Uma at the end of the game
     * @return created game
     */
    public Game startGame(GamePlayers gamePlayers,
                          RulesSet rulesSet,
                          int startPoints,
                          Set<Wind> windsToPlay,
                          boolean withUma) {
        List<Player> players = getOrderedPlayers(gamePlayers);

        Game game = Game.newGame(players, rulesSet.getCode(), startPoints, windsToPlay, withUma);
        game.getGameData().getRounds().add(
                Round.start(players.get(0).getId(), Wind.EAST)
        );
        return gameRepo.create(game);
    }

    /**
     * @param gamePlayers players to participate
     * @return players in the wind's order
     */
    private List<Player> getOrderedPlayers(GamePlayers gamePlayers) {
        BiMap<Wind, Player> windToPlayer = HashBiMap.create(gamePlayers.getWindToPlayer());
        List<Player> playersWithoutWind = gamePlayers.getPlayers().values().stream()
                .filter(p -> !windToPlayer.inverse().containsKey(p))
                .collect(toList());
        Collections.shuffle(playersWithoutWind);
        Iterator<Player> playersIter = playersWithoutWind.iterator();

        List<Player> out = new ArrayList<>();
        for (Wind wind : Wind.getOrdered()) {
            if (windToPlayer.containsKey(wind)) {
                out.add(windToPlayer.get(wind));
            } else {
                Preconditions.checkState(playersIter.hasNext(), "Not enough players!");
                out.add(playersIter.next());
            }
        }
        return out;
    }

    /**
     * Update the game with specified round score and return update version. If dry run set to true,
     * the updated version won't be stored into database.
     *
     * @param game   game to update
     * @param score  current round score
     * @param dryRun is it dry run
     * @return updated game with next round started, if applicable by rules set
     */
    public Game roundComplete(Game game, RoundScore score, boolean dryRun) {
        Preconditions.checkArgument(!game.isCompleted(), "Game already completed");
        // Check that there are scores for all players
        game.getPlayerIds().forEach(playerId -> {
            Preconditions.checkArgument(score.contains(playerId),
                    "No score for player %s", playerId);
        });

        GameData gameData = game.getGameData();
        // At least one round exists by game creation algorithm.
        Round currentRound = gameData.getLastRound();
        GameSeating seating = new GameSeating(game.getPlayerIds());

        Optional<RulesSet> rulesSetOpt = rulesSetRegistry.getRulesSet(gameData.getRulesSetCode());
        Preconditions.checkArgument(rulesSetOpt.isPresent(),
                "Rules set %s not found", gameData.getRulesSetCode());
        RulesSet rulesSet = rulesSetOpt.get();

        // Set current round results.
        rulesSet.calculateRoundScore(score, currentRound, seating);
        currentRound.setRawScores(score.getPlayerIdToScore());

        // Get honba and riichi sticks for next round.
        int riichiSticksCount = currentRound.getRiichiSticksCount();
        int honbaSticksCount = currentRound.getHonbaSticksCount();
        if (!score.getWinners().isEmpty()) {
            // There is winner, so no more riichi on the table
            riichiSticksCount = 0;
            if (score.getWinners().contains(currentRound.getDealerId())) {
                // Dealer is among the winners
                honbaSticksCount++;
            } else {
                honbaSticksCount = 0;
            }
        } else {
            // It is draw
            PlayerScore dealerScore = score.getPlayerIdToScore().get(currentRound.getDealerId());
            if (dealerScore.isTempai() || dealerScore.isRiichi()) {
                // Keep dealer
                honbaSticksCount++;
            } else {
                // Switch dealer
                honbaSticksCount = 0;
            }
            // Add all riichi on the table to riichi sticks count
            for (PlayerScore s : score.getPlayerIdToScore().values()) {
                if (s.isRiichi()) {
                    riichiSticksCount++;
                }
            }
        }

        Wind nextRoundWind = getNextRoundWind(currentRound, seating);
        Wind lastGameWind = gameData.getLastGameWind();
        RulesSet.GameEndOptions gameEndOptions = null;
        if (nextRoundWind == lastGameWind.next()) {
            // It was the last round in the last wind of the game.
            // We should check the rules, shoud we continue playing?
            gameEndOptions = rulesSet.canCompleteGame(game.getCurrentScoreByPlayer(),
                    currentRound.getDealerId(),
                    honbaSticksCount > 0);
        }

        if (gameEndOptions != null) {
            switch (gameEndOptions) {
                case END:
                    return updateGame(completeGame(game), dryRun);
                case REPEAT_ROUND:
                    gameData.getRounds().add(Round.start(
                            currentRound.getDealerId(), currentRound.getWind(), riichiSticksCount, honbaSticksCount));
                    return updateGame(game, dryRun);
                case PLAY_NEXT_WIND:
                    throw new NotImplementedException("Play next wind after last wind in the game currently not supported");
            }
        }

        // Game needn't be complete at this point
        if (honbaSticksCount > 0) {
            // It means that we need to repeat last round
            gameData.getRounds().add(Round.start(currentRound.getDealerId(), currentRound.getWind(), riichiSticksCount, honbaSticksCount));
            return updateGame(game, dryRun);
        }

        // We need to switch dealer
        Long nextDealerId = getNextDealerId(currentRound, seating);
        Round nextRound = Round.start(nextDealerId, nextRoundWind, riichiSticksCount, honbaSticksCount);

        gameData.getRounds().add(nextRound);
        return updateGame(game, dryRun);
    }

    private Game updateGame(Game newVersion, boolean dryRun) {
        if (dryRun) {
            return newVersion;
        }
        return gameRepo.update(newVersion);
    }

    private Long getNextDealerId(Round currentRound, GameSeating seating) {
        return seating.getPlayerOn(
                // Get current dealer wind in seating and peek player on next wind
                seating.getWindFor(currentRound.getDealerId()).next()
        );
    }

    private Wind getNextRoundWind(Round currentRound, GameSeating seating) {
        boolean needSwitchWind = seating.getWindFor(currentRound.getDealerId()) == Wind.NORTH;
        return !needSwitchWind ? currentRound.getWind() : currentRound.getWind().next();
    }

    private Game completeGame(Game game) {
        List<Integer> currentScore = game.getCurrentScore();
        if (game.getGameData().isWithUma()) {
            applyUma(currentScore, game.getPlayerToPlace());
        }
        applyPenalties(currentScore, game.getPlayerIds(), game.getGameData().getPenalties());
        game.setFinalScore(currentScore);
        game.setCompleted(true);
        return game;
    }

    private void applyUma(List<Integer> score, BiMap<Integer, Integer> playerToPlace) {
        List<Integer> uma = Arrays.asList(15000, 5000, -5000, 15000);
        for (int i = 0; i < score.size(); i++) {
            score.set(i, score.get(i) + uma.get(playerToPlace.get(i)));
        }
    }

    private void applyPenalties(List<Integer> score, List<Long> playerIds, List<Penalty> penalties) {
        Map<Long, Integer> playerToPos = new HashMap<>();
        for (int i = 0; i < playerIds.size(); i++) {
            playerToPos.put(playerIds.get(i), i);
        }
        penalties.forEach(penalty -> {
            int playerPos = playerToPos.get(penalty.getPlayerId());
            score.set(playerPos, score.get(playerPos) - penalty.getAmount());
        });
    }

    private <T> T getLast(List<T> l) {
        Preconditions.checkArgument(!l.isEmpty(), "List is empty!");
        return l.get(l.size() - 1);
    }

}
