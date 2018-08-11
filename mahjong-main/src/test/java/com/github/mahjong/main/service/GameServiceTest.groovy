package com.github.mahjong.main.service

import com.github.mahjong.common.enums.LangIso639
import com.github.mahjong.main.model.Game
import com.github.mahjong.main.model.Player
import com.github.mahjong.main.model.PlayerScore
import com.github.mahjong.main.model.Wind
import com.github.mahjong.main.repo.GameRepo
import com.github.mahjong.main.rules.RulesSet
import com.github.mahjong.main.rules.RulesSetRegistry
import com.github.mahjong.main.rules.riichi.ema.RiichiEMACombination
import com.github.mahjong.main.rules.riichi.ema.RiichiEMARuleSet
import com.github.mahjong.main.service.model.GamePlayers
import com.github.mahjong.main.service.model.RoundScore
import spock.lang.Specification

class GameServiceTest extends Specification {

    def "roundComplete; ron; switch dialer"() {
        given:
        def service = new GameService(riichiEmaRulesSetRegistry(), createUpdateOnlyGameRepo())
        def game = service.startGame(gamePlayers(), new RiichiEMARuleSet(), 30000, [Wind.EAST] as Set, false)
        def score = new RoundScore(
                [
                        1L: new PlayerScore([], 0, 0, false, false, false),
                        2L: new PlayerScore([RiichiEMACombination.RIICHI.code], 0, 40, false, true, false),
                        3L: new PlayerScore([], 0, 0, false, false, false),
                        4L: new PlayerScore([], 0, 0, false, false, false),
                ],
                [2L] as Set,
                [1L] as Set
        )

        when:
        def updated = service.roundComplete(game, score, false)

        then:
        updated.gameData.rounds.size() == 2
        updated.gameData.lastRound.dealerId == 2L
        updated.gameData.lastRound.wind == Wind.EAST
        updated.gameData.lastRound.riichiSticksCount == 0
        updated.gameData.lastRound.honbaSticksCount == 0
        updated.gameData.rounds[0].scores != null
        updated.gameData.rounds[0].scores.size() == 4
        updated.gameData.rounds[0].rawScores != null
        updated.gameData.rounds[0].rawScores.size() == 4
        updated.gameData.rounds[0].riichiSticksCount == 0
        updated.gameData.rounds[0].honbaSticksCount == 0
    }

    def "roundComplete; ron; keep dialer"() {
        given:
        def service = new GameService(riichiEmaRulesSetRegistry(), createUpdateOnlyGameRepo())
        def game = service.startGame(gamePlayers(), new RiichiEMARuleSet(), 30000, [Wind.EAST] as Set, false)
        def score = new RoundScore(
                [
                        1L: new PlayerScore([RiichiEMACombination.RIICHI.code], 0, 40, false, true, false),
                        2L: new PlayerScore([], 0, 0, false, false, false),
                        3L: new PlayerScore([], 0, 0, false, false, false),
                        4L: new PlayerScore([], 0, 0, false, false, false),
                ],
                [1L] as Set,
                [2L] as Set
        )

        when:
        def updated = service.roundComplete(game, score, false)

        then:
        updated.gameData.rounds.size() == 2
        updated.gameData.lastRound.dealerId == 1L
        updated.gameData.lastRound.wind == Wind.EAST
        updated.gameData.lastRound.riichiSticksCount == 0
        updated.gameData.lastRound.honbaSticksCount == 1
        updated.gameData.rounds[0].scores != null
        updated.gameData.rounds[0].scores.size() == 4
        updated.gameData.rounds[0].rawScores != null
        updated.gameData.rounds[0].rawScores.size() == 4
        updated.gameData.rounds[0].riichiSticksCount == 0
        updated.gameData.rounds[0].honbaSticksCount == 0
    }

    def "roundComplete; draw; switch dialer"() {
        given:
        def service = new GameService(riichiEmaRulesSetRegistry(), createUpdateOnlyGameRepo())
        def game = service.startGame(gamePlayers(), new RiichiEMARuleSet(), 30000, [Wind.EAST] as Set, false)
        def score = new RoundScore(
                [
                        1L: new PlayerScore([], 0, 40, false, false, false),
                        2L: new PlayerScore([], 0, 0, false, true, true),
                        3L: new PlayerScore([], 0, 0, false, false, false),
                        4L: new PlayerScore([], 0, 0, false, true, true),
                ],
                [] as Set,
                [] as Set
        )

        when:
        def updated = service.roundComplete(game, score, false)

        then:
        updated.gameData.rounds.size() == 2
        updated.gameData.lastRound.dealerId == 2L
        updated.gameData.lastRound.wind == Wind.EAST
        updated.gameData.lastRound.riichiSticksCount == 2
        updated.gameData.lastRound.honbaSticksCount == 0
        // todo don't check it every time, make separate test that previous round was completed
        updated.gameData.rounds[0].scores != null
        updated.gameData.rounds[0].scores.size() == 4
        updated.gameData.rounds[0].rawScores != null
        updated.gameData.rounds[0].rawScores.size() == 4
        updated.gameData.rounds[0].riichiSticksCount == 0
        updated.gameData.rounds[0].honbaSticksCount == 0
    }

    def "roundComplete; draw; keep dialer"() {
        given:
        def service = new GameService(riichiEmaRulesSetRegistry(), createUpdateOnlyGameRepo())
        def game = service.startGame(gamePlayers(), new RiichiEMARuleSet(), 30000, [Wind.EAST] as Set, false)
        def score = new RoundScore(
                [
                        1L: new PlayerScore([], 0, 40, false, false, true),
                        2L: new PlayerScore([], 0, 0, false, true, true),
                        3L: new PlayerScore([], 0, 0, false, false, false),
                        4L: new PlayerScore([], 0, 0, false, false, false),
                ],
                [] as Set,
                [] as Set
        )

        when:
        def updated = service.roundComplete(game, score, false)

        then:
        updated.gameData.rounds.size() == 2
        updated.gameData.lastRound.dealerId == 1L
        updated.gameData.lastRound.wind == Wind.EAST
        updated.gameData.lastRound.riichiSticksCount == 1
        updated.gameData.lastRound.honbaSticksCount == 1
        updated.gameData.rounds[0].scores != null
        updated.gameData.rounds[0].scores.size() == 4
        updated.gameData.rounds[0].rawScores != null
        updated.gameData.rounds[0].rawScores.size() == 4
        updated.gameData.rounds[0].riichiSticksCount == 0
        updated.gameData.rounds[0].honbaSticksCount == 0
    }

    /**
     * @return 4 players with ids from 1 to 4 and seating 1 - EAST, 2 - SOUTH, 3 - WEST, 4 - NORTH
     */
    def gamePlayers() {
        def p1 = new Player(1L, "p1", "p1", LangIso639.EN)
        def p2 = new Player(2L, "p2", "p2", LangIso639.EN)
        def p3 = new Player(3L, "p3", "p3", LangIso639.EN)
        def p4 = new Player(4L, "p4", "p4", LangIso639.EN)
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

    def riichiEmaRulesSetRegistry() {
        return new RulesSetRegistry() {

            private final RulesSet riichiEmaRulesSet = new RiichiEMARuleSet()

            @Override
            Collection<RulesSet> getRegistered() {
                return [riichiEmaRulesSet]
            }

            @Override
            Optional<RulesSet> getRulesSet(String code) {
                if (!Objects.equals(code, riichiEmaRulesSet.getCode())) {
                    return Optional.empty()
                }
                return Optional.of(riichiEmaRulesSet)
            }
        }
    }
}
