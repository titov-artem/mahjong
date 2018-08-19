package com.github.mahjong.main.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.*;

import static java.util.stream.Collectors.toList;

@NotThreadSafe
@Data
@AllArgsConstructor
public class Game {

    private final long id;
    private final List<Long> playerIds;
    private GameData gameData;
    private List<Integer> finalScore;
    private boolean isCompleted;

    public static Game newGame(List<Player> players,
                               String rulesSetCode,
                               int startPoints,
                               Set<Wind> windsToPlay,
                               boolean withUma) {
        Preconditions.checkArgument(players.size() == 4, "Only 4 players can play mahjong");
        return new Game(-1,
                players.stream().map(Player::getId).collect(toList()),
                GameData.newData(rulesSetCode, startPoints, windsToPlay, withUma),
                new ArrayList<>(),
                false
        );
    }

    public List<Integer> getCurrentScore() {
        List<Integer> scores = playerIds.stream().map(p -> getGameData().getStartPoints()).collect(toList());
        for (Round round : getGameData().getRounds()) {
            for (int i = 0; i < round.getScores().size(); i++) {
                scores.set(i, scores.get(i) + round.getScores().get(playerIds.get(i)));
            }
        }
        return scores;
    }

    public Map<Long, Integer> getCurrentScoreByPlayer() {
        List<Integer> scores = getCurrentScore();
        Map<Long, Integer> out = new HashMap<>();
        for (int i = 0; i < playerIds.size(); i++) {
            out.put(playerIds.get(i), scores.get(i));
        }
        return out;
    }

    public void setFinalScore(Map<Long, Integer> score) {
        this.finalScore = new ArrayList<>(playerIds.size());
        for (Long playerId : playerIds) {
            finalScore.add(score.get(playerId));
        }
    }

    /**
     * @return map from player id to his place
     */
    public BiMap<Long, Integer> getPlayerToPlace() {
        // TODO rewrite it on easier solution
        List<Integer> currentScore = getCurrentScore();
        List<Pair<Integer, Integer>> scoreWithPlayerIdx = new ArrayList<>();
        for (int i = 0; i < currentScore.size(); i++) {
            scoreWithPlayerIdx.add(Pair.of(currentScore.get(i), i));
        }
        scoreWithPlayerIdx.sort((p1, p2) -> {
            int cmpRes = p1.getLeft().compareTo(p2.getLeft());
            if (cmpRes != 0) {
                return cmpRes;
            }
            return p1.getRight().compareTo(p2.getRight());
        });

        BiMap<Long, Integer> out = HashBiMap.create();
        for (int i = 0; i < scoreWithPlayerIdx.size(); i++) {
            out.put(
                    playerIds.get(scoreWithPlayerIdx.get(i).getRight()),
                    i + 1  // place
            );
        }
        return out;
    }
}
