package com.github.mahjong.main.service

import com.github.mahjong.common.enums.LangIso639
import com.github.mahjong.common.json.JsonUtil
import com.github.mahjong.main.model.Game
import com.github.mahjong.main.model.Player
import com.github.mahjong.main.model.PlayerScore
import com.github.mahjong.main.model.Wind
import com.github.mahjong.main.repo.GameRepo
import com.github.mahjong.main.rules.riichi.ema.RiichiEMACombination
import com.github.mahjong.main.rules.riichi.ema.RiichiEMARuleSet
import com.github.mahjong.main.service.model.GamePlayers
import com.github.mahjong.main.service.model.RoundScore
import spock.lang.Specification

class GameServiceWithRiichiEMAFullGameTest extends Specification {

    def "full game;"() {
        given:
        def service = new GameService(new RiichiEmaRulesSetRegistry(), createUpdateOnlyGameRepo())
        def game = service.startGame(gamePlayers(), new RiichiEMARuleSet(), 30000, [Wind.EAST, Wind.SOUTH] as Set, true)

        // 1.40, 2.40, 3.40, 4.40, 5, 6, 7, 8 for not dealer
        def expectedWinsPoints = [1300, 2600, 5200, 8000, 8000, 12000, 12000, 16000]
        def expectedLoosePoints = expectedWinsPoints.collect { -it }
        def expectedDealerSequence = [0L, 1L, 2L, 3L, 0L, 1L, 2L, 3L]
        def expectedWiners = []
        def expectedLoosers = []

        when:
        def nextWinner = 1L
        for (int i = 0; i < 8; i++) {
            def scoreMap = notenScoreMap()
            scoreMap[nextWinner] = x40Score(i + 1)
            def score = new RoundScore(
                    scoreMap,
                    [nextWinner] as Set,
                    [(nextWinner - 1 + 4) % 4] as Set
            )

            expectedWiners.add(nextWinner)
            expectedLoosers.add((nextWinner - 1 + 4) % 4)
            game = service.roundComplete(game, score, false)

            nextWinner = (nextWinner + 1) % 4
        }
        println game.finalScore
        println JsonUtil.writeValue(game.gameData).toString()

        then:
        game.completed
        game.finalScore.sum() == 120000
        game.gameData.rounds.every { it.wind in [Wind.EAST, Wind.SOUTH] }
        game.gameData.rounds.every { it.riichiSticksCount == 0 }
        game.gameData.rounds.every { it.honbaSticksCount == 0 }
        game.gameData.rounds.collect { it.dealerId } == expectedDealerSequence
        game.gameData.rounds.collect { getAnyPositivePoints(it.scores).key } == expectedWiners
        game.gameData.rounds.collect { getAnyPositivePoints(it.scores).value } == expectedWinsPoints
        game.gameData.rounds.collect { getAnyNegativePoints(it.scores).key } == expectedLoosers
        game.gameData.rounds.collect { getAnyNegativePoints(it.scores).value } == expectedLoosePoints
    }

    def "full game; with twice win"() {
        given:
        def service = new GameService(new RiichiEmaRulesSetRegistry(), createUpdateOnlyGameRepo())
        def game = service.startGame(gamePlayers(), new RiichiEMARuleSet(), 30000, [Wind.EAST, Wind.SOUTH] as Set, true)

        // 1.40, 2.40, 3.40, 4.40, 5, 6, 7, 8 for dealer first and then for not dealer
        def expectedWinsPoints = [
                2000, 1600,
                3900, 2900,
                7700, 5500,
                12000, 8300,
                12000, 8300,
                18000, 12300,
                18000, 12300,
                24000, 16300
        ]
        def expectedLoosePoints = expectedWinsPoints.collect { -it }
        def expectedDealerSequence = [0L, 0L, 1L, 1L, 2L, 2L, 3L, 3L, 0L, 0L, 1L, 1L, 2L, 2L, 3L, 3L]
        def expectedHonbaSequence = (0..15).collect { it % 2 }
        def expectedWiners = []
        def expectedLoosers = []

        when:
        def nextWinner = 0L
        for (int i = 0; i < 8; i++) {
            def scoreMap = notenScoreMap()
            scoreMap[nextWinner] = x40Score(i + 1)
            def score = new RoundScore(
                    scoreMap,
                    [nextWinner] as Set,
                    [(nextWinner - 1 + 4) % 4] as Set
            )
            expectedWiners.add(nextWinner)
            expectedLoosers.add((nextWinner - 1 + 4) % 4)
            game = service.roundComplete(game, score, false)
            nextWinner = (nextWinner + 1) % 4

            scoreMap = notenScoreMap()
            scoreMap[nextWinner] = x40Score(i + 1)
            score = new RoundScore(
                    scoreMap,
                    [nextWinner] as Set,
                    [(nextWinner - 1 + 4) % 4] as Set
            )
            expectedWiners.add(nextWinner)
            expectedLoosers.add((nextWinner - 1 + 4) % 4)
            game = service.roundComplete(game, score, false)
        }
        println game.finalScore
        println JsonUtil.writeValue(game.gameData).toString()

        then:
        game.completed
        game.finalScore.sum() == 120000
        game.gameData.rounds.every { it.wind in [Wind.EAST, Wind.SOUTH] }
        game.gameData.rounds.every { it.riichiSticksCount == 0 }
        game.gameData.rounds.collect { it.honbaSticksCount } == expectedHonbaSequence
        game.gameData.rounds.collect { it.dealerId } == expectedDealerSequence
        game.gameData.rounds.collect { getAnyPositivePoints(it.scores).key } == expectedWiners
        game.gameData.rounds.collect { getAnyPositivePoints(it.scores).value } == expectedWinsPoints
        game.gameData.rounds.collect { getAnyNegativePoints(it.scores).key } == expectedLoosers
        game.gameData.rounds.collect { getAnyNegativePoints(it.scores).value } == expectedLoosePoints
    }

    def "full game; 15 2x riichi draw, last dealer win and win non dealer"() {
        given:
        def service = new GameService(new RiichiEmaRulesSetRegistry(), createUpdateOnlyGameRepo())
        def game = service.startGame(gamePlayers(), new RiichiEMARuleSet(), 30000, [Wind.EAST, Wind.SOUTH] as Set, true)

        def expectedDealerSequence = [0L, 0L, 1L, 1L, 2L, 2L, 3L, 3L, 0L, 0L, 1L, 1L, 2L, 2L, 3L, 3L]
        def expectedHonbaSequence = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]
        def expectedRiichiSequence = [0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 0]

        when:
        // East and West riichi end with draw (+1 honba), then South and North riichi end with draw switch dealer
        def dealer = 0L
        for (int i = 0; i < 7; i++) {
            def scoreMap = notenScoreMap()
            scoreMap[dealer] = riichiTempaiScore()
            scoreMap[(dealer + 2) % 4] = riichiTempaiScore()
            def score = new RoundScore(
                    scoreMap,
                    [] as Set,
                    [] as Set
            )
            game = service.roundComplete(game, score, false)

            scoreMap = notenScoreMap()
            scoreMap[(dealer + 1) % 4] = riichiTempaiScore()
            scoreMap[(dealer + 3) % 4] = riichiTempaiScore()
            score = new RoundScore(
                    scoreMap,
                    [] as Set,
                    [] as Set
            )
            game = service.roundComplete(game, score, false)

            dealer = (dealer + 1) % 4
        }
        def scoreMap = notenScoreMap()
        scoreMap[3L] = x40Score(1)
        def score = new RoundScore(
                scoreMap,
                [3L] as Set,
                [2L] as Set
        )
        game = service.roundComplete(game, score, false)

        scoreMap = notenScoreMap()
        scoreMap[0L] = x40Score(1)
        score = new RoundScore(
                scoreMap,
                [0L] as Set,
                [3L] as Set
        )
        game = service.roundComplete(game, score, false)

        println game.finalScore
        println JsonUtil.writeValue(game.gameData).toString()

        then:
        game.completed
        game.finalScore.sum() == 120000
        game.gameData.rounds.every { it.wind in [Wind.EAST, Wind.SOUTH] }
        game.gameData.rounds.collect { it.riichiSticksCount } == expectedRiichiSequence
        game.gameData.rounds.collect { it.honbaSticksCount } == expectedHonbaSequence
        game.gameData.rounds.collect { it.dealerId } == expectedDealerSequence
    }

    def getAnyPositivePoints(Map<Long, Integer> scores) {
        for (Map.Entry<Long, Integer> entry : scores.entrySet()) {
            if (entry.getValue() > 0) return entry
        }
        throw new AssertionError("No positive score!")
    }

    def getAnyNegativePoints(Map<Long, Integer> scores) {
        for (Map.Entry<Long, Integer> entry : scores.entrySet()) {
            if (entry.getValue() < 0) return entry
        }
        throw new AssertionError("No negative score!")
    }

    def areXZeros(Map<Long, Integer> scores, int x) {
        def zeroCount = 0
        for (Integer points : scores.values()) {
            if (points == 0) zeroCount++
        }
        return zeroCount == x
    }

    def notenScoreMap() {
        def scoreMap = new HashMap<>()
        for (long j = 0L; j < 4L; j++) {
            scoreMap[j] = notenScore()
        }
        scoreMap
    }

    def x40Score(int hanCount) {
        new PlayerScore(
                [RiichiEMACombination.RIICHI.code],
                hanCount >= 1 ? hanCount - 1 : 0,
                hanCount <= 4 ? 40 : 0,
                false,
                true,
                false)
    }

    def riichiTempaiScore() {
        return new PlayerScore([], 0, 0, false, true, true)
    }

    def notenScore() {
        return new PlayerScore([], 0, 0, false, false, false)
    }

    /**
     * @return 4 players with ids from 1 to 4 and seating 1 - EAST, 2 - SOUTH, 3 - WEST, 4 - NORTH
     */
    def gamePlayers() {
        def p1 = new Player(0L, "p1", "p1", LangIso639.EN)
        def p2 = new Player(1L, "p2", "p2", LangIso639.EN)
        def p3 = new Player(2L, "p3", "p3", LangIso639.EN)
        def p4 = new Player(3L, "p4", "p4", LangIso639.EN)
        return new GamePlayers(
                [
                        (p1.id): p1,
                        (p2.id): p2,
                        (p3.id): p3,
                        (p4.id): p4,
                ],
                [
                        (Wind.EAST) : p1,
                        (Wind.SOUTH): p2,
                        (Wind.WEST) : p3,
                        (Wind.NORTH): p4,
                ]
        )
    }

    def createUpdateOnlyGameRepo() {
        return Mock(GameRepo) {
            create(_) >> { Game game -> return game }
            update(_) >> { Game game -> return game }
        }
    }

}
