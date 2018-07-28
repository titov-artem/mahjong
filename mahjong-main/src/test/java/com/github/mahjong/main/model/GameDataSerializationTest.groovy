package com.github.mahjong.main.model

import com.github.mahjong.common.json.JsonUtil
import com.github.mahjong.main.rules.riichi.ema.RiichiEMACombination
import com.github.mahjong.main.rules.riichi.ema.RiichiEMARuleSet
import org.apache.commons.lang3.builder.EqualsBuilder
import spock.lang.Specification

class GameDataSerializationTest extends Specification {

    def testSerialization() {
        given:
        def roundId = UUID.randomUUID().toString()
        def data = new GameData(
                RiichiEMARuleSet.RIICHI_EMA_RULES_CODE,
                30000,
                [Wind.SOUTH, Wind.EAST] as Set,
                false,
                [
                        new Round(
                                roundId,
                                1,
                                Wind.EAST,
                                0,
                                0,
                                [
                                        1: 1500,
                                        2: -1500,
                                        3: 0,
                                        4: 0,
                                ],
                                [
                                        1: new PlayerScore([RiichiEMACombination.RIICHI.toString()], 0, 0, false, true, false),
                                        2: new PlayerScore([], 0, 0, false, false, false),
                                        3: new PlayerScore([], 0, 0, false, false, false),
                                        4: new PlayerScore([], 0, 0, false, false, false),
                                ],
                        ),
                        new Round(
                                roundId,
                                1,
                                Wind.EAST,
                                0,
                                0,
                                new HashMap<Long, Integer>(),
                                new HashMap<Long, PlayerScore>(),
                        )
                ],
                [new Penalty(3, roundId, Penalty.Type.DEAD_HAND, 0)]
        )

        when:
        def parsedData = JsonUtil.readValue(JsonUtil.writeValue(data), GameData)

        then:
        Objects.deepEquals(JsonUtil.writeValue(data), JsonUtil.writeValue(parsedData))
    }

}
