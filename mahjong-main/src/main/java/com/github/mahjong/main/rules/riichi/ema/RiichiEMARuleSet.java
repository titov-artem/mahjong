package com.github.mahjong.main.rules.riichi.ema;

import com.github.mahjong.common.enums.LangIso639;
import com.github.mahjong.common.translation.DefaultTranslationMessageSource;
import com.github.mahjong.common.translation.TranslationMessageSource;
import com.github.mahjong.main.model.*;
import com.github.mahjong.main.rules.Combination;
import com.github.mahjong.main.rules.RulesSet;
import com.github.mahjong.main.rules.riichi.RiichiBasicScoreHelper;
import com.github.mahjong.main.service.model.GameSeating;
import com.github.mahjong.main.service.model.RoundScore;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.mahjong.main.rules.Combination.SCORE_LIMIT;
import static com.github.mahjong.main.rules.riichi.RiichiBasicScoreHelper.roundPoints;
import static com.github.mahjong.main.rules.riichi.ema.RiichiEMACombination.RIICHI;
import static com.github.mahjong.main.rules.riichi.ema.RiichiEMACombination.TSUMO;

@Service
public class RiichiEMARuleSet implements RulesSet {

    public static final String RIICHI_EMA_RULES_CODE = "RIICHI_EMA";

    private final TranslationMessageSource messageSource;

    @Inject
    public RiichiEMARuleSet() {
        this.messageSource = new DefaultTranslationMessageSource("translation/rules/riichi_ema");
    }

    @Override
    public String getCode() {
        return RIICHI_EMA_RULES_CODE;
    }

    @Override
    public String getName(LangIso639 lang) {
        return messageSource.getTranslated(getTranslationCodePrefix() + ".name", lang, LangIso639.EN);
    }

    @Override
    public String getDescription(LangIso639 lang) {
        return messageSource.getTranslated(getTranslationCodePrefix() + ".description", lang, LangIso639.EN);
    }

    @Override
    public String getCombinationName(Combination combination, LangIso639 lang) {
        if (!(combination instanceof RiichiEMACombination)) {
            throw new IllegalArgumentException(String.format("This rules set doesn't support combination of class %s: %s",
                    combination.getClass(), combination));
        }
        RiichiEMACombination c = (RiichiEMACombination) combination;
        return messageSource.getTranslated(getTranslationCodePrefix() + ".combinations." + c.name().toLowerCase(),
                lang, LangIso639.EN);
    }

    private static String getTranslationCodePrefix() {
        return String.format("rules.%s", RIICHI_EMA_RULES_CODE.toLowerCase());
    }

    @Override
    public List<Combination> getAvailableCombinations() {
        return Arrays.asList(RiichiEMACombination.values());
    }

    @Override
    public Optional<Combination> getRiichiCombination() {
        return Optional.of(RIICHI);
    }

    @Override
    public Map<Long, Integer> calculateRoundScore(RoundScore roundScore,
                                                  Round round,
                                                  GameSeating seating) {
        if (roundScore.isDraw()) {
            return processDraw(roundScore);
        }
        Preconditions.checkArgument(
                (roundScore.getLosers().size() == 1 && roundScore.getWinners().size() >= 1) // Ron
                        || (roundScore.getLosers().size() == 3 && roundScore.getWinners().size() == 1), // Tsumo
                "Unexpected amount of winners and losers"
        );

        Map<Long, Integer> scores = new HashMap<>();
        roundScore.getPlayerIdToScore().keySet().forEach(playerId -> scores.put(playerId, 0));
        // At first we will pay to winners for combinations only with honba. After we will pay riichi sticks from table
        roundScore.getWinners().forEach(winnerId -> {
            PlayerScore winnerScore = roundScore.getPlayerIdToScore().get(winnerId);
            boolean isDealer = winnerId == round.getDealerId();
            boolean isTsumo = winnerScore.getCombinationCodes().contains(TSUMO.getCode());
            int basicPoints = getBasicPoints(winnerScore);

            if (isDealer) {
                if (isTsumo) {
                    int basicPayment = roundPoints(basicPoints * 2) + 100 * round.getHonbaSticksCount();
                    addScore(scores, winnerId, 3 * basicPayment);
                    roundScore.getPlayerIdToScore().keySet().stream()
                            .filter(playerId -> !Objects.equals(playerId, winnerId))
                            .forEach(playerId -> addScore(scores, playerId, -basicPayment));
                } else {
                    Preconditions.checkArgument(roundScore.getLosers().size() == 1, "Dealer can't ron from more than one player");
                    int payment = roundPoints(6 * basicPoints) + 300 * round.getHonbaSticksCount();
                    addScore(scores, winnerId, payment);
                    addScore(scores, first(roundScore.getLosers()), -payment);
                }
            } else {
                if (isTsumo) {
                    addScore(scores, winnerId, roundPoints(basicPoints * 2) + 2 * roundPoints(basicPoints) + 300 * round.getHonbaSticksCount());
                    roundScore.getPlayerIdToScore().keySet()
                            .forEach(playerId -> {
                                if (Objects.equals(playerId, winnerId)) {
                                    return;
                                }
                                if (Objects.equals(playerId, round.getDealerId())) {
                                    addScore(scores, playerId, -roundPoints(basicPoints * 2) - 100 * round.getHonbaSticksCount());
                                } else {
                                    addScore(scores, playerId, -roundPoints(basicPoints) - 100 * round.getHonbaSticksCount());
                                }
                            });
                } else {
                    Preconditions.checkArgument(roundScore.getLosers().size() == 1, "Player can't ron from more than one player");
                    int payment = roundPoints(basicPoints * 4) + 300 * round.getHonbaSticksCount();
                    addScore(scores, winnerId, payment);
                    addScore(scores, first(roundScore.getLosers()), -payment);
                }
            }

        });
        // Validate payments
        {
            int totalDelta = 0;
            for (Integer payment : scores.values()) {
                totalDelta += payment;
            }
            Preconditions.checkState(totalDelta == 0, "Payed amount is not equal received amount!");
        }
        // Pay riichi sticks on the table
        int riichiCount = round.getRiichiSticksCount();
        // Add riichi sticks from players in riichi and substract 1000 points from these players.
        for (Map.Entry<Long, PlayerScore> entry : roundScore.getPlayerIdToScore().entrySet()) {
            PlayerScore score = entry.getValue();
            if (score.isRiichi()) {
                riichiCount++;
                addScore(scores, entry.getKey(), -1000);
            }
        }

        // Winners get their riichi back. Other riichi sticks go to the first winner
        for (Long winnerId : roundScore.getWinners()) {
            PlayerScore winnerScore = roundScore.getPlayerIdToScore().get(winnerId);
            if (winnerScore.isRiichi()) {
                riichiCount--;
                addScore(scores, winnerId, 1000);
            }
        }

        //noinspection ConstantConditions: there is at least one winner
        Long firstWinnerId = roundScore.getWinners().stream()
                .map(seating::getWindFor)
                .sorted()
                .findFirst()
                .map(seating::getPlayerOn)
                .get();
        addScore(scores, firstWinnerId, riichiCount * 1000);
        return scores;
    }

    @Override
    public GameEndOptions shouldCompleteGame(Game game,
                                             boolean isDealerSucceed) {
        if (game.getGameData().getLastRound().getDealerId() != last(game.getPlayerIds())) {
            // wind not completed
            return GameEndOptions.CONTINUE;
        }
        // Wind completed
        if (isDealerSucceed) {
            return GameEndOptions.CONTINUE;
        }
        // Dealer loose
        Wind lastWind = game.getGameData().getLastGameWind();
        Wind currentWind = game.getGameData().getLastRound().getWind();
        if (lastWind != currentWind) {
            // Not all winds played
            return GameEndOptions.CONTINUE;
        }
        return GameEndOptions.END;
    }

    @Override
    public Map<Long, Integer> calculateFinalScore(Game game, int riichiSticksCount) {
        Map<Long, Integer> playerToScore = game.getCurrentScoreByPlayer();
        // Add left riichi sticks to 1st place
        BiMap<Long, Integer> playerToPlace = game.getPlayerToPlace();
        addScore(playerToScore, playerToPlace.inverse().get(1), riichiSticksCount * 1000);
        if (game.getGameData().isWithUma()) {
            applyUma(playerToScore, playerToPlace);
        }
        applyPenalties(playerToScore, game.getGameData().getPenalties());
        return playerToScore;
    }

    private void applyUma(Map<Long, Integer> playerToScore, BiMap<Long, Integer> playerToPlace) {
        List<Integer> uma = Arrays.asList(15000, 5000, -5000, -15000);
        for (Long playerId : new ArrayList<>(playerToScore.keySet())) {
            addScore(playerToScore, playerId, uma.get(playerToPlace.get(playerId) - 1));
        }
    }

    private void applyPenalties(Map<Long, Integer> playerToScore, List<Penalty> penalties) {
        penalties.forEach(penalty -> addScore(playerToScore, penalty.getPlayerId(), -penalty.getAmount()));
    }

    private int getBasicPoints(PlayerScore playerScore) {
        Set<RiichiEMACombination> combinations = playerScore.getCombinationCodes().stream()
                .map(RiichiEMACombination::forCode)
                .collect(Collectors.toSet());
        int hanCount = 0;
        for (RiichiEMACombination c : combinations) {
            int score = c.getScore(playerScore.isOpenHand());
            if (score == SCORE_LIMIT) {
                hanCount = SCORE_LIMIT;
                break;
            }
            hanCount += score;
        }
        Preconditions.checkArgument(playerScore.getDoraCount() >= 0, "Dora count can't be less then 0");
        hanCount += playerScore.getDoraCount();
        if (hanCount >= 13 && hanCount != SCORE_LIMIT) {
            // No kazoe-yakuman
            hanCount = 12;
        }
        if (hanCount == SCORE_LIMIT) {
            // Yakuman
            hanCount = 13;
        }
        Preconditions.checkArgument(playerScore.getFuCount() >= 0, "Fu count can't be less then 0");
        return RiichiBasicScoreHelper.getBasicPoints(hanCount, playerScore.getFuCount());
    }

    // todo add tests
    @VisibleForTesting
    protected Map<Long, Integer> processDraw(RoundScore roundScore) {
        Preconditions.checkState(roundScore.isDraw());
        Map<Long, Integer> score = new HashMap<>();
        // Substract 1000 for players with riichi
        roundScore.getPlayerIdToScore().forEach((playerId, sc) -> {
            if (sc.isRiichi()) {
                score.put(playerId, -1000);
            } else {
                score.put(playerId, 0);
            }
        });
        // Get players in tempai
        Set<Long> playersInTempai = new HashSet<>();
        roundScore.getPlayerIdToScore().forEach((playerId, sc) -> {
            if (sc.isTempai() || sc.isRiichi()) {
                playersInTempai.add(playerId);
            }
        });
        if (playersInTempai.size() == 4 || playersInTempai.size() == 0) {
            // If all or none in tempai, then we need to pay nothing
            return score;
        }
        // How much player in tempai will receive from others
        int incomeSize = 3000 / playersInTempai.size();
        // How much player without tempai will pay to others
        int outcomeSize = 3000 / (4 - playersInTempai.size());
        roundScore.getPlayerIdToScore().forEach((playerId, sc) -> {
            int curScore = score.get(playerId);
            if (playersInTempai.contains(playerId)) {
                score.put(playerId, curScore + incomeSize);
            } else {
                score.put(playerId, curScore - outcomeSize);
            }
        });
        return score;
    }

    private static void addScore(Map<Long, Integer> playerToScore, Long playerId, int scoreDelta) {
        if (!playerToScore.containsKey(playerId)) {
            playerToScore.put(playerId, 0);
        }
        playerToScore.put(playerId, playerToScore.get(playerId) + scoreDelta);
    }

    private static <T> T first(Iterable<T> it) {
        Iterator<T> iterator = it.iterator();
        Preconditions.checkArgument(iterator.hasNext(), "Iterable is empty!");
        return iterator.next();
    }

    private static <T> T last(List<T> col) {
        Preconditions.checkArgument(!col.isEmpty(), "Iterable is empty!");
        return col.get(col.size() - 1);
    }
}
