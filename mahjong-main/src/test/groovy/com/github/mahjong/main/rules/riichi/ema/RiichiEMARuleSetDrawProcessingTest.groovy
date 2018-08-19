package com.github.mahjong.main.rules.riichi.ema

import com.github.mahjong.main.model.PlayerScore
import com.github.mahjong.main.service.model.RoundScore
import spock.lang.Specification

class RiichiEMARuleSetDrawProcessingTest extends Specification {

    RiichiEMARuleSet ruleSet = new RiichiEMARuleSet()

    def "processDraw; 0 tempai without any riichi"() {
        given:
        def score = new RoundScore(
                [
                        1L: tempaiScore(false, false),
                        2L: tempaiScore(false, false),
                        3L: tempaiScore(false, false),
                        4L: tempaiScore(false, false),
                ],
                [] as Set,
                [] as Set
        )

        when:
        def resultScores = ruleSet.processDraw(score)

        then:
        resultScores == new HashMap([
                1L: 0,
                2L: 0,
                3L: 0,
                4L: 0,
        ])
    }

    def "processDraw; 1 tempai without any riichi"() {
        given:
        def score = new RoundScore(
                [
                        1L: tempaiScore(false, false),
                        2L: tempaiScore(false, true),
                        3L: tempaiScore(false, false),
                        4L: tempaiScore(false, false),
                ],
                [] as Set,
                [] as Set
        )

        when:
        def resultScores = ruleSet.processDraw(score)

        then:
        resultScores == new HashMap([
                1L: -1000,
                2L: 3000,
                3L: -1000,
                4L: -1000,
        ])
    }

    def "processDraw; 2 tempai without any riichi"() {
        given:
        def score = new RoundScore(
                [
                        1L: tempaiScore(false, false),
                        2L: tempaiScore(false, true),
                        3L: tempaiScore(false, true),
                        4L: tempaiScore(false, false),
                ],
                [] as Set,
                [] as Set
        )

        when:
        def resultScores = ruleSet.processDraw(score)

        then:
        resultScores == new HashMap([
                1L: -1500,
                2L: 1500,
                3L: 1500,
                4L: -1500,
        ])
    }

    def "processDraw; 3 tempai without any riichi"() {
        given:
        def score = new RoundScore(
                [
                        1L: tempaiScore(false, true),
                        2L: tempaiScore(false, true),
                        3L: tempaiScore(false, true),
                        4L: tempaiScore(false, false),
                ],
                [] as Set,
                [] as Set
        )

        when:
        def resultScores = ruleSet.processDraw(score)

        then:
        resultScores == new HashMap([
                1L: 1000,
                2L: 1000,
                3L: 1000,
                4L: -3000,
        ])
    }

    def "processDraw; 4 tempai without any riichi"() {
        given:
        def score = new RoundScore(
                [
                        1L: tempaiScore(false, true),
                        2L: tempaiScore(false, true),
                        3L: tempaiScore(false, true),
                        4L: tempaiScore(false, true),
                ],
                [] as Set,
                [] as Set
        )

        when:
        def resultScores = ruleSet.processDraw(score)

        then:
        resultScores == new HashMap([
                1L: 0,
                2L: 0,
                3L: 0,
                4L: 0,
        ])
    }

    def "processDraw; 1 tempai with 1 riichi"() {
        given:
        def score = new RoundScore(
                [
                        1L: tempaiScore(false, false),
                        2L: tempaiScore(true, true),
                        3L: tempaiScore(false, false),
                        4L: tempaiScore(false, false),
                ],
                [] as Set,
                [] as Set
        )

        when:
        def resultScores = ruleSet.processDraw(score)

        then:
        resultScores == new HashMap([
                1L: -1000,
                2L: 2000,
                3L: -1000,
                4L: -1000,
        ])
    }

    def "processDraw; 2 tempai with 2 riichi"() {
        given:
        def score = new RoundScore(
                [
                        1L: tempaiScore(true, true),
                        2L: tempaiScore(true, true),
                        3L: tempaiScore(false, false),
                        4L: tempaiScore(false, false),
                ],
                [] as Set,
                [] as Set
        )

        when:
        def resultScores = ruleSet.processDraw(score)

        then:
        resultScores == new HashMap([
                1L: 500,
                2L: 500,
                3L: -1500,
                4L: -1500,
        ])
    }

    def "processDraw; 3 tempai with 3 riichi"() {
        given:
        def score = new RoundScore(
                [
                        1L: tempaiScore(false, false),
                        2L: tempaiScore(true, true),
                        3L: tempaiScore(true, true),
                        4L: tempaiScore(true, true),
                ],
                [] as Set,
                [] as Set
        )

        when:
        def resultScores = ruleSet.processDraw(score)

        then:
        resultScores == new HashMap([
                1L: -3000,
                2L: 0,
                3L: 0,
                4L: 0,
        ])
    }

    def "processDraw; 4 tempai with 4 riichi"() {
        given:
        def score = new RoundScore(
                [
                        1L: tempaiScore(true, true),
                        2L: tempaiScore(true, true),
                        3L: tempaiScore(true, true),
                        4L: tempaiScore(true, true),
                ],
                [] as Set,
                [] as Set
        )

        when:
        def resultScores = ruleSet.processDraw(score)

        then:
        resultScores == new HashMap([
                1L: -1000,
                2L: -1000,
                3L: -1000,
                4L: -1000,
        ])
    }

    def "processDraw; 0 tempai without any riichi and 2 riichi and 2 honba on the table"() {
        given:
        def score = new RoundScore(
                [
                        1L: tempaiScore(false, false),
                        2L: tempaiScore(false, false),
                        3L: tempaiScore(false, false),
                        4L: tempaiScore(false, false),
                ],
                [] as Set,
                [] as Set
        )

        when:
        def resultScores = ruleSet.processDraw(score)

        then:
        resultScores == new HashMap([
                1L: 0,
                2L: 0,
                3L: 0,
                4L: 0,
        ])
    }

    private static PlayerScore tempaiScore(boolean riichi, boolean tempai) {
        return new PlayerScore([], 0, 0, false, riichi, tempai);
    }
}
