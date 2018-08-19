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
        Map<Long, Integer> roundScores = rulesSet.calculateRoundScore(score, currentRound, seating);
        currentRound.setScores(roundScores);
        currentRound.setRawScores(score.getPlayerIdToScore());

        // Get honba and riichi sticks for next round.
        int riichiSticksCount = currentRound.getRiichiSticksCount();
        int honbaSticksCount = currentRound.getHonbaSticksCount();
        boolean dealerSucceed = false;
        if (!score.getWinners().isEmpty()) {
            // There is winner, so no more riichi on the table
            riichiSticksCount = 0;
            if (score.getWinners().contains(currentRound.getDealerId())) {
                // Dealer is among the winners
                honbaSticksCount++;
                dealerSucceed = true;
            } else {
                honbaSticksCount = 0;
            }
        } else {
            // It is draw
            PlayerScore dealerScore = score.getPlayerIdToScore().get(currentRound.getDealerId());
            if (dealerScore.isTempai() || dealerScore.isRiichi()) {
                // Keep dealer
                dealerSucceed = true;
            }
            honbaSticksCount++;
            // Add all riichi on the table to riichi sticks count
            for (PlayerScore s : score.getPlayerIdToScore().values()) {
                if (s.isRiichi()) {
                    riichiSticksCount++;
                }
            }
        }

        // We should check the rules, shoud we continue playing?
        RulesSet.GameEndOptions gameEndOptions = rulesSet.shouldCompleteGame(game, dealerSucceed);
        switch (gameEndOptions) {
            case CONTINUE:
                // We need do nothing here if we need to continue
                break;
            case END:
                // Game can end at any point, for example because of bankruptcy of one player
                return updateGame(completeGame(game, riichiSticksCount, rulesSet), dryRun);
            case PLAY_NEXT_WIND:
                throw new NotImplementedException("Play next wind after last wind in the game currently not supported");
        }

        // Game needn't to be complete at this point
        if (dealerSucceed) {
            // It means that we need to repeat last round
            gameData.getRounds().add(Round.start(
                    currentRound.getDealerId(), currentRound.getWind(), riichiSticksCount, honbaSticksCount));
            return updateGame(game, dryRun);
        }

        // We need to switch dealer
        Long nextDealerId = getNextDealerId(currentRound, seating);
        gameData.getRounds().add(Round.start(
                nextDealerId, getNextRoundWind(currentRound, seating), riichiSticksCount, honbaSticksCount));

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

    private Game completeGame(Game game, int riichiSticksCount, RulesSet rulesSet) {
        game.setFinalScore(rulesSet.calculateFinalScore(game, riichiSticksCount));
        game.setCompleted(true);
        return game;
    }
}
