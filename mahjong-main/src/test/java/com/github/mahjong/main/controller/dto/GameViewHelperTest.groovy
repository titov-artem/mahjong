package com.github.mahjong.main.controller.dto

import com.github.mahjong.api.common.dto.PlayerShortView
import com.github.mahjong.api.common.dto.WindDto
import com.github.mahjong.common.enums.LangIso639
import com.github.mahjong.main.model.Game
import com.github.mahjong.main.model.GameData
import com.github.mahjong.main.model.Player
import com.github.mahjong.main.model.Round
import com.github.mahjong.main.model.Wind
import spock.lang.Specification

class GameViewHelperTest extends Specification {


    def "from"() {
        given:
        def game = new Game(
                1L,
                [1L, 2L, 3L, 4L],
                new GameData(
                        'rules',
                        30000,
                        [Wind.EAST] as Set,
                        false,
                        [
                                new Round(
                                        '1',
                                        1L,
                                        Wind.EAST,
                                        0,
                                        0,
                                        [
                                                1L: -1000,
                                                2L: -1000,
                                                3L: -1000,
                                                4L: 3000,
                                        ],
                                        [:]
                                ),
                                new Round(
                                        '1',
                                        2L,
                                        Wind.EAST,
                                        1,
                                        2,
                                        [:],
                                        [:]
                                )
                        ],
                        []
                ),
                [],
                false
        )
        def players = [
                1L: player(1L, 'a'),
                2L: player(2L, 'b'),
                3L: player(3L, 'c'),
                4L: player(4L, 'd'),
        ]

        when:
        def view = GameViewHelper.from(game, players)

        then:
        view.id == 1L
        view.rulesSetCode == 'rules'
        view.players == new HashMap([
                (WindDto.EAST) : new PlayerShortView(id: 1L, name: 'a'),
                (WindDto.SOUTH): new PlayerShortView(id: 2L, name: 'b'),
                (WindDto.WEST) : new PlayerShortView(id: 3L, name: 'c'),
                (WindDto.NORTH): new PlayerShortView(id: 4L, name: 'd'),
        ])
        view.scores == new HashMap([
                (WindDto.EAST) : 29000,
                (WindDto.SOUTH): 29000,
                (WindDto.WEST) : 29000,
                (WindDto.NORTH): 33000,
        ])
        view.windMapping == new HashMap([
                (WindDto.EAST) : WindDto.NORTH,
                (WindDto.SOUTH): WindDto.EAST,
                (WindDto.WEST) : WindDto.SOUTH,
                (WindDto.NORTH): WindDto.WEST,
        ])
        view.riichiSticksOnTable == 1
        view.honbaCount == 2
        view.currentDealer == 2L
        view.currentWind == WindDto.EAST
        view.currentWindRoundNumber == 2
        view.windsToPlay == [WindDto.EAST] as Set
        view.startPoints == 30000
        view.withUma == false
        view.completed == false
    }

    private static Player player(Long id, String name) {
        return new Player(id, name, name, LangIso639.EN, name);
    }

}
