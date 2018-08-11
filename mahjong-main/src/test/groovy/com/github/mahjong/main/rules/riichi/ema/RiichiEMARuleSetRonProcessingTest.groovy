package com.github.mahjong.main.rules.riichi.ema

import com.github.mahjong.main.model.Round
import com.github.mahjong.main.model.Wind
import com.github.mahjong.main.service.model.GameSeating
import com.github.mahjong.main.model.PlayerScore
import com.github.mahjong.main.service.model.RoundScore
import spock.lang.Specification

import static com.github.mahjong.main.rules.riichi.ema.RiichiEMACombination.*

class RiichiEMARuleSetRonProcessingTest extends Specification {

    RiichiEMARuleSet ruleSet = new RiichiEMARuleSet()

    def "calculateRoundScore; non dealer ron 3-30 with own riichi"() {
        given:
        def round = round()
        def score = new RoundScore(
                [
                        1L: riichiScore(false),
                        2L: new PlayerScore([PINFU.code, RIICHI.code, IPPATSU.code], 0, 30, false, true, false),
                        3L: riichiScore(false),
                        4L: riichiScore(false),
                ],
                [2L] as Set,
                [1L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        ruleSet.calculateRoundScore(score, round, seating)

        then:
        round.scores == new HashMap([
                1L: -3900,
                2L: 3900,
                3L: 0,
                4L: 0,
        ])
    }

    def "calculateRoundScore; non dealer ron 3-30 with looser and own riichi"() {
        given:
        def round = round()
        def score = new RoundScore(
                [
                        1L: riichiScore(true),
                        2L: new PlayerScore([PINFU.code, RIICHI.code, IPPATSU.code], 0, 30, false, true, false),
                        3L: riichiScore(false),
                        4L: riichiScore(false),
                ],
                [2L] as Set,
                [1L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        ruleSet.calculateRoundScore(score, round, seating)

        then:
        round.scores == new HashMap([
                1L: -4900,
                2L: 4900,
                3L: 0,
                4L: 0,
        ])
    }

    def "calculateRoundScore; non dealer ron 3-30 with looser and own riichi and 2 riichi on the table"() {
        given:
        def round = round()
        round.riichiSticksCount = 2
        def score = new RoundScore(
                [
                        1L: riichiScore(true),
                        2L: new PlayerScore([PINFU.code, RIICHI.code, IPPATSU.code], 0, 30, false, true, false),
                        3L: riichiScore(false),
                        4L: riichiScore(false),
                ],
                [2L] as Set,
                [1L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        ruleSet.calculateRoundScore(score, round, seating)

        then:
        round.scores == new HashMap([
                1L: -4900,
                2L: 6900,
                3L: 0,
                4L: 0,
        ])
    }

    def "calculateRoundScore; non dealer ron 3-30 with looser and own riichi and 2 honba on the table"() {
        given:
        def round = round()
        round.honbaSticksCount = 2
        def score = new RoundScore(
                [
                        1L: riichiScore(true),
                        2L: new PlayerScore([PINFU.code, RIICHI.code, IPPATSU.code], 0, 30, false, true, false),
                        3L: riichiScore(false),
                        4L: riichiScore(false),
                ],
                [2L] as Set,
                [1L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        ruleSet.calculateRoundScore(score, round, seating)

        then:
        round.scores == new HashMap([
                1L: -5500,
                2L: 5500,
                3L: 0,
                4L: 0,
        ])
    }

    def "calculateRoundScore; non dealer ron 3-30 with non looser and own riichi"() {
        given:
        def round = round()
        def score = new RoundScore(
                [
                        1L: riichiScore(false),
                        2L: new PlayerScore([PINFU.code, RIICHI.code, IPPATSU.code], 0, 30, false, true, false),
                        3L: riichiScore(true),
                        4L: riichiScore(false),
                ],
                [2L] as Set,
                [1L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        ruleSet.calculateRoundScore(score, round, seating)

        then:
        round.scores == new HashMap([
                1L: -3900,
                2L: 4900,
                3L: -1000,
                4L: 0,
        ])
    }

    def "calculateRoundScore; dealer ron 3-30 with non loosers and non own riichi"() {
        given:
        def round = round()
        def score = new RoundScore(
                [
                        1L: new PlayerScore([PINFU.code, TANYAO.code], 1, 30, false, false, false),
                        2L: riichiScore(false),
                        3L: riichiScore(true),
                        4L: riichiScore(true),
                ],
                [1L] as Set,
                [2L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        ruleSet.calculateRoundScore(score, round, seating)

        then:
        round.scores == new HashMap([
                1L: 7800,
                2L: -5800,
                3L: -1000,
                4L: -1000,
        ])
    }

    def "calculateRoundScore; double ron dealer and non dealer no riichi"() {
        given:
        def round = round()
        def score = new RoundScore(
                [
                        1L: new PlayerScore([PINFU.code, TANYAO.code], 0, 30, false, false, false),
                        2L: riichiScore(false),
                        3L: riichiScore(false),
                        4L: new PlayerScore([YAKUHAI_3.code], 0, 30, true, false, false),
                ],
                [1L, 4L] as Set,
                [3L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        ruleSet.calculateRoundScore(score, round, seating)

        then:
        round.scores == new HashMap([
                1L: 2900,
                2L: 0,
                3L: -6800,
                4L: 3900,
        ])
    }

    def "calculateRoundScore; double ron dealer and non dealer no riichi, 2 riichi on the table"() {
        given:
        def round = round()
        round.riichiSticksCount = 2
        def score = new RoundScore(
                [
                        1L: new PlayerScore([PINFU.code, TANYAO.code], 0, 30, false, false, false),
                        2L: riichiScore(false),
                        3L: riichiScore(false),
                        4L: new PlayerScore([YAKUHAI_3.code], 0, 30, true, false, false),
                ],
                [1L, 4L] as Set,
                [3L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        ruleSet.calculateRoundScore(score, round, seating)

        then:
        round.scores == new HashMap([
                1L: 4900,
                2L: 0,
                3L: -6800,
                4L: 3900,
        ])
    }

    def "calculateRoundScore; double ron dealer and non dealer non dealer winner with riichi"() {
        given:
        def round = round()
        def score = new RoundScore(
                [
                        1L: new PlayerScore([PINFU.code, TANYAO.code], 0, 30, false, false, false),
                        2L: riichiScore(false),
                        3L: riichiScore(false),
                        4L: new PlayerScore([YAKUHAI_2.code, RIICHI.code], 0, 30, false, true, false),
                ],
                [1L, 4L] as Set,
                [3L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        ruleSet.calculateRoundScore(score, round, seating)

        then:
        round.scores == new HashMap([
                1L: 2900,
                2L: 0,
                3L: -6800,
                4L: 3900,
        ])
    }

    def "calculateRoundScore; double ron non dealers dealer and loser in riichi, second winner in riichi"() {
        given:
        def round = round()
        def score = new RoundScore(
                [
                        1L: riichiScore(true),
                        2L: riichiScore(true),
                        3L: new PlayerScore([PINFU.code, TANYAO.code], 0, 30, false, false, false),
                        4L: new PlayerScore([YAKUHAI_2.code, RIICHI.code], 0, 30, false, true, false),
                ],
                [3L, 4L] as Set,
                [2L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        ruleSet.calculateRoundScore(score, round, seating)

        then:
        round.scores == new HashMap([
                1L: -1000,
                2L: -6900,
                3L: 4000,
                4L: 3900,
        ])
    }

    def "calculateRoundScore; triple ron non dealer, dealer riichi"() {
        given:
        def round = round()
        def score = new RoundScore(
                [
                        1L: riichiScore(true),
                        2L: new PlayerScore([PINFU.code, TANYAO.code], 0, 30, false, false, false),
                        3L: new PlayerScore([PINFU.code, TANYAO.code], 0, 30, false, false, false),
                        4L: new PlayerScore([YAKUHAI_3.code], 0, 30, true, false, false),
                ],
                [2L, 3L, 4L] as Set,
                [1L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        ruleSet.calculateRoundScore(score, round, seating)

        then:
        round.scores == new HashMap([
                1L: -8900,
                2L: 3000,
                3L: 2000,
                4L: 3900,
        ])
    }

    def "calculateRoundScore; triple ron non dealer, dealer riichi, 1 honba on the table"() {
        given:
        def round = round()
        round.honbaSticksCount = 1
        def score = new RoundScore(
                [
                        1L: riichiScore(true),
                        2L: new PlayerScore([PINFU.code, TANYAO.code], 0, 30, false, false, false),
                        3L: new PlayerScore([PINFU.code, TANYAO.code], 0, 30, false, false, false),
                        4L: new PlayerScore([YAKUHAI_3.code], 0, 30, true, false, false),
                ],
                [2L, 3L, 4L] as Set,
                [1L] as Set
        )
        def seating = new GameSeating([1L, 2L, 3L, 4L])

        when:
        ruleSet.calculateRoundScore(score, round, seating)

        then:
        round.scores == new HashMap([
                1L: -9800,
                2L: 3300,
                3L: 2300,
                4L: 4200,
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
