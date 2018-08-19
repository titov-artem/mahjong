package com.github.mahjong.main.rules.riichi.ema

import com.github.mahjong.main.model.PlayerScore
import com.github.mahjong.main.model.Round
import com.github.mahjong.main.model.Wind
import com.github.mahjong.main.service.model.GameSeating
import com.github.mahjong.main.service.model.RoundScore
import spock.lang.Specification

import static com.github.mahjong.main.rules.riichi.ema.RiichiEMACombination.*

class RiichiEMARuleSetTsumoProcessingTest extends Specification {

    RiichiEMARuleSet ruleSet = new RiichiEMARuleSet()

    def "calculateRoundScore; non dealer tsumo 3-25 without riichi"() {
        given:
        def round = round()
        def score = new RoundScore(
                [
                        1L: riichiScore(false),
                        2L: new PlayerScore([CHIITOITSU.code, TSUMO.code], 0, 25, false, false, false),
                        3L: riichiScore(false),
                        4L: riichiScore(false),
                ],
                [2L] as Set,
                [1L, 3L, 4L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        def resultScores = ruleSet.calculateRoundScore(score, round, seating)

        then:
        resultScores == new HashMap([
                1L: -1600,
                2L: 3200,
                3L: -800,
                4L: -800,
        ])
    }

    def "calculateRoundScore; non dealer tsumo 3-30 with own riichi"() {
        given:
        def round = round()
        def score = new RoundScore(
                [
                        1L: riichiScore(false),
                        2L: new PlayerScore([PINFU.code, RIICHI.code, TSUMO.code], 0, 30, false, true, false),
                        3L: riichiScore(false),
                        4L: riichiScore(false),
                ],
                [2L] as Set,
                [1L, 3L, 4L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        def resultScores = ruleSet.calculateRoundScore(score, round, seating)

        then:
        resultScores == new HashMap([
                1L: -2000,
                2L: 4000,
                3L: -1000,
                4L: -1000,
        ])
    }

    def "calculateRoundScore; non dealer tsumo 4-30 with own riichi"() {
        given:
        def round = round()
        def score = new RoundScore(
                [
                        1L: riichiScore(false),
                        2L: new PlayerScore([TANYAO.code, PINFU.code, RIICHI.code, TSUMO.code], 0, 30, false, true, false),
                        3L: riichiScore(false),
                        4L: riichiScore(false),
                ],
                [2L] as Set,
                [1L, 3L, 4L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        def resultScores = ruleSet.calculateRoundScore(score, round, seating)

        then:
        resultScores == new HashMap([
                1L: -3900,
                2L: 7900,
                3L: -2000,
                4L: -2000,
        ])
    }

    def "calculateRoundScore; non dealer tsumo 4-40 with own riichi and 1 dora"() {
        given:
        def round = round()
        def score = new RoundScore(
                [
                        1L: riichiScore(false),
                        2L: new PlayerScore([YAKUHAI_1.code, RIICHI.code, TSUMO.code], 1, 40, false, true, false),
                        3L: riichiScore(false),
                        4L: riichiScore(false),
                ],
                [2L] as Set,
                [1L, 3L, 4L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        def resultScores = ruleSet.calculateRoundScore(score, round, seating)

        then:
        resultScores == new HashMap([
                1L: -4000,
                2L: 8000,
                3L: -2000,
                4L: -2000,
        ])
    }

    def "calculateRoundScore; non dealer tsumo 6 without riichi and 5 dora"() {
        given:
        def round = round()
        def score = new RoundScore(
                [
                        1L: riichiScore(false),
                        2L: new PlayerScore([TSUMO.code], 5, 30, false, false, false),
                        3L: riichiScore(false),
                        4L: riichiScore(false),
                ],
                [2L] as Set,
                [1L, 3L, 4L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        def resultScores = ruleSet.calculateRoundScore(score, round, seating)

        then:
        resultScores == new HashMap([
                1L: -6000,
                2L: 12000,
                3L: -3000,
                4L: -3000,
        ])
    }

    def "calculateRoundScore; non dealer tsumo 8 with own riichi"() {
        given:
        def round = round()
        def score = new RoundScore(
                [
                        1L: riichiScore(false),
                        2L: new PlayerScore([RIICHI.code, TSUMO.code, IPPATSU.code, SANSHOKU.code, JUNCHAN.code], 0, 30, false, true, false),
                        3L: riichiScore(false),
                        4L: riichiScore(false),
                ],
                [2L] as Set,
                [1L, 3L, 4L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        def resultScores = ruleSet.calculateRoundScore(score, round, seating)

        then:
        resultScores == new HashMap([
                1L: -8000,
                2L: 16000,
                3L: -4000,
                4L: -4000,
        ])
    }

    def "calculateRoundScore; non dealer tsumo 13 non kazoe yakuman with own riichi"() {
        given:
        def round = round()
        def score = new RoundScore(
                [
                        1L: riichiScore(false),
                        2L: new PlayerScore([RIICHI.code, TSUMO.code, IPPATSU.code,
                                             CHINITSU.code, HONROUTOU.code], 2, 60, false, true, false),
                        3L: riichiScore(false),
                        4L: riichiScore(false),
                ],
                [2L] as Set,
                [1L, 3L, 4L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        def resultScores = ruleSet.calculateRoundScore(score, round, seating)

        then:
        resultScores == new HashMap([
                1L: -12000,
                2L: 24000,
                3L: -6000,
                4L: -6000,
        ])
    }

    def "calculateRoundScore; non dealer tsumo yakuman"() {
        given:
        def round = round()
        def score = new RoundScore(
                [
                        1L: riichiScore(false),
                        2L: new PlayerScore([TSUMO.code, DAISANGEN.code], 0, 60, false, false, false),
                        3L: riichiScore(false),
                        4L: riichiScore(false),
                ],
                [2L] as Set,
                [1L, 3L, 4L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        def resultScores = ruleSet.calculateRoundScore(score, round, seating)

        then:
        resultScores == new HashMap([
                1L: -16000,
                2L: 32000,
                3L: -8000,
                4L: -8000,
        ])
    }

    def "calculateRoundScore; non dealer tsumo 4-30 with dealer riichi"() {
        given:
        def round = round()
        def score = new RoundScore(
                [
                        1L: riichiScore(true),
                        2L: new PlayerScore([TANYAO.code, PINFU.code, RIICHI.code, TSUMO.code], 0, 30, false, true, false),
                        3L: riichiScore(false),
                        4L: riichiScore(false),
                ],
                [2L] as Set,
                [1L, 3L, 4L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        def resultScores = ruleSet.calculateRoundScore(score, round, seating)

        then:
        resultScores == new HashMap([
                1L: -4900,
                2L: 8900,
                3L: -2000,
                4L: -2000,
        ])
    }

    def "calculateRoundScore; dealer tsumo 4-30 with non dealer riichi"() {
        given:
        def round = round()
        def score = new RoundScore(
                [
                        1L: new PlayerScore([TANYAO.code, PINFU.code, RIICHI.code, TSUMO.code], 0, 30, false, true, false),
                        2L: riichiScore(true),
                        3L: riichiScore(false),
                        4L: riichiScore(false),
                ],
                [1L] as Set,
                [2L, 3L, 4L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        def resultScores = ruleSet.calculateRoundScore(score, round, seating)

        then:
        resultScores == new HashMap([
                1L: 12700,
                2L: -4900,
                3L: -3900,
                4L: -3900,
        ])
    }

    def "calculateRoundScore; dealer tsumo 4-30 with non dealer riichi and 2 riichi on the table"() {
        given:
        def round = round()
        round.riichiSticksCount = 2
        def score = new RoundScore(
                [
                        1L: new PlayerScore([TANYAO.code, PINFU.code, RIICHI.code, TSUMO.code], 0, 30, false, true, false),
                        2L: riichiScore(true),
                        3L: riichiScore(false),
                        4L: riichiScore(false),
                ],
                [1L] as Set,
                [2L, 3L, 4L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        def resultScores = ruleSet.calculateRoundScore(score, round, seating)

        then:
        resultScores == new HashMap([
                1L: 14700,
                2L: -4900,
                3L: -3900,
                4L: -3900,
        ])
    }

    def "calculateRoundScore; dealer tsumo 4-30 with non dealer riichi and 1 honba on the table"() {
        given:
        def round = round()
        round.honbaSticksCount = 1
        def score = new RoundScore(
                [
                        1L: new PlayerScore([TANYAO.code, PINFU.code, RIICHI.code, TSUMO.code], 0, 30, false, true, false),
                        2L: riichiScore(true),
                        3L: riichiScore(false),
                        4L: riichiScore(false),
                ],
                [1L] as Set,
                [2L, 3L, 4L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        def resultScores = ruleSet.calculateRoundScore(score, round, seating)

        then:
        resultScores == new HashMap([
                1L: 13000,
                2L: -5000,
                3L: -4000,
                4L: -4000,
        ])
    }

    private static PlayerScore riichiScore(boolean riichi) {
        return new PlayerScore([], 0, 0, false, riichi, false);
    }

    private static Round round() {
        return new Round(
                '1',
                1L,
                Wind.EAST,
                0,
                0,
                [:],
                [:]
        )
    }
}
