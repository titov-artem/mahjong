package com.github.mahjong.main.controller.publicapi.impl;

import com.github.mahjong.api.common.dto.GameView;
import com.github.mahjong.main.controller.dto.GameViewHelper;
import com.github.mahjong.main.exceptions.GameNotFoundException;
import com.github.mahjong.main.exceptions.RulesSetNotFoundException;
import com.github.mahjong.main.model.Game;
import com.github.mahjong.main.model.Player;
import com.github.mahjong.main.publicapi.GameController;
import com.github.mahjong.main.publicapi.dto.RoundScoreDto;
import com.github.mahjong.main.repo.GameRepo;
import com.github.mahjong.main.repo.PlayerRepo;
import com.github.mahjong.main.rules.Combination;
import com.github.mahjong.main.rules.RulesSet;
import com.github.mahjong.main.rules.RulesSetRegistry;
import com.github.mahjong.main.service.GameService;
import com.github.mahjong.main.model.PlayerScore;
import com.github.mahjong.main.service.model.RoundScore;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.validation.constraints.Max;
import java.util.*;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

@Controller("publicGameController")
public class GameControllerImpl extends AbstractPlayerAwareController implements GameController {

    private final GameService gameService;
    private final RulesSetRegistry rulesSetRegistry;
    private final GameRepo gameRepo;

    @Inject
    public GameControllerImpl(GameService gameService,
                              RulesSetRegistry rulesSetRegistry,
                              GameRepo gameRepo,
                              PlayerRepo playerRepo) {
        super(playerRepo);
        this.gameService = gameService;
        this.rulesSetRegistry = rulesSetRegistry;
        this.gameRepo = gameRepo;
    }

    @Override
    public List<GameView> getAllActive() {
        Optional<Player> playerOpt = playerRepo.getByLogin(getCurrentPlayerLogin());
        if (!playerOpt.isPresent()) {
            return Collections.emptyList();
        }
        List<Game> games = gameRepo.getActiveByPlayer(playerOpt.get());
        Set<Long> playerIds = games.stream()
                .flatMap(g -> g.getPlayerIds().stream())
                .collect(toSet());
        Map<Long, Player> players = playerRepo.getAll(playerIds).stream()
                .collect(toMap(Player::getId, identity()));
        return games.stream().map(game -> GameViewHelper.from(game, players)).collect(toList());
    }

    @Override
    public List<GameView> getLastGames(Integer count) {
        return null;
    }

    @Override
    public GameView get(Long id) {
        Game game = gameRepo.get(id).orElseThrow(GameNotFoundException.supplier(id));
        return GameViewHelper.from(
                game,
                playerRepo.getAll(game.getPlayerIds()).stream()
                        .collect(toMap(Player::getId, identity()))
        );
    }

    @Override
    public GameView roundComplete(Long id, RoundScoreDto score, Boolean dryRun) {
        Game game = gameRepo.get(id).orElseThrow(GameNotFoundException.supplier(id));
        RulesSet rulesSet = rulesSetRegistry.getRulesSet(game.getGameData().getRulesSetCode())
                .orElseThrow(RulesSetNotFoundException.supplier(game.getGameData().getRulesSetCode()));
        RoundScore roundScore = new RoundScore(
                score.scores.stream()
                        .peek(ps -> {
                            Optional<Combination> riichiOpt = rulesSet.getRiichiCombination();
                            if (riichiOpt.isPresent()) {
                                // If rules support riichi, ensure that ps.riichi and ps.combinations are
                                // synchronized.
                                Combination riichi = riichiOpt.get();
                                if (ps.riichi) {
                                    if (!ps.combinationCodes.contains(riichi.getCode())) {
                                        ps.combinationCodes.add(riichi.getCode());
                                    }
                                } else if (ps.combinationCodes.contains(riichi.getCode())) {
                                    ps.riichi = true;
                                }
                            }
                            if (ps.riichi && score.winners.isEmpty() && score.losers.isEmpty()) {
                                // Ensure that in round with draw player with riichi has tempai.
                                ps.tempai = true;
                            }
                        })
                        .collect(toMap(
                                ps -> ps.playerId,
                                ps -> new PlayerScore(
                                        ps.combinationCodes,
                                        ps.doraCount,
                                        ps.fuCount,
                                        ps.openHand,
                                        ps.riichi,
                                        ps.tempai
                                )
                        )),
                score.winners,
                score.losers
        );
        Game updatedGame = gameService.roundComplete(
                game,
                roundScore,
                dryRun
        );
        return GameViewHelper.from(
                updatedGame,
                playerRepo.getAll(game.getPlayerIds()).stream()
                        .collect(toMap(Player::getId, identity()))
        );
    }

}
