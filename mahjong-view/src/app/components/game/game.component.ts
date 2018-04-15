import {Component, Input, OnInit} from '@angular/core';
import {GameService} from '../../services/game.service';
import {GameView} from '../../model/game.view';
import {ActivatedRoute, Router} from '@angular/router';
import {Wind, WindHelper} from '../../model/wind';
import {PlayerResult} from './model/player.result';
import {RoundScoreDto} from '../../model/round-score-dto';
import {PlayerScoreDto} from '../../model/player-score-dto';
import {PlayerCombinations} from './model/player.combinations';
import {PlayerShort} from '../../model/player.short';

@Component({
    selector: 'game',
    templateUrl: './game.component.html',
    styleUrls: ['./game.component.css']
})
export class GameComponent implements OnInit {

    game: GameView = null;

    constructor(private route: ActivatedRoute,
                private router: Router,
                private gameService: GameService) {
    }

    ngOnInit() {
        this.getGame();
    }

    private getGame() {
        const id = +this.route.snapshot.paramMap.get('id');
        this.gameService.get(id).subscribe(game => this.game = game);
    }

    /* Methods and fields related to current game score view */
    recountedScore: Map<Wind, number> = null;

    getCurrentWindSymbol(): string {
        return this.getWindSymbol(this.game.currentWind);
    }

    private getWindSymbol(wind: Wind) {
        switch (wind) {
            case Wind.EAST:
                return '東';
            case Wind.SOUTH:
                return '南';
            case Wind.WEST:
                return '西';
            case Wind.NORTH:
                return '北';
        }
        return ''
    }

    /*
     * Seating methods. These methods accept initial player wind, not current one.
     */
    getPlayerNameOn(rawWind: string): string {
        return this.game.players.get(Wind[rawWind]).name;
    }

    getPlayerIdOn(rawWind: string): number {
        return this.game.players.get(Wind[rawWind]).id;
    }

    getScoreOn(rawWind: string): number {
        let wind = Wind[rawWind];
        if (this.recountedScore != null) {
            return this.recountedScore.get(wind);
        }
        return this.game.scores.get(wind);
    }

    getCurrentWindOn(rawWind: string): string {
        let originWind = Wind[rawWind];
        const currentWind = this.game.windMapping[originWind];
        return this.getWindSymbol(currentWind);
    }

    recountFor(rawWind: string) {
        if (this.recountedScore != null) {
            this.recountedScore = null;
            return;
        }
        let wind = Wind[rawWind];
        this.recountedScore = new Map<Wind, number>();
        for (let w of WindHelper.getOrdered()) {
            if (w == wind) {
                this.recountedScore.set(w, this.game.scores.get(wind))
            } else {
                this.recountedScore.set(w, this.game.scores.get(w) - this.game.scores.get(wind));
            }
        }
        console.log(this.recountedScore);
    }

    /* Seating methods end. */

    /* Methods and fields related to round completion */
    isRoundComplete = false;
    isDraw = false;
    isTsumo = false;
    isRon = false;

    showCombinationSelect = false;
    combinationSelectPlayerId = null;

    resultByPlayerId = new Map<number, PlayerResult>();
    combinationsByPlayerId = new Map<number, PlayerCombinations>();

    reset(game: GameView) {
        this.game = game;
        this.roundComplete();
        this.isRoundComplete = false;
        this.showCombinationSelect = false;
        this.combinationSelectPlayerId = null;
    }

    draw() {
        this.roundComplete();
        this.isDraw = true;
    }

    ron() {
        this.roundComplete();
        this.isRon = true;
    }

    tsumo() {
        this.roundComplete();
        this.isTsumo = true;
    }

    roundContinue() {
        this.roundComplete();
        this.isRoundComplete = false;
    }

    private roundComplete() {
        this.isRoundComplete = true;
        this.resultByPlayerId.clear();
        this.combinationsByPlayerId.clear();
        this.isRon = false;
        this.isTsumo = false;
        this.isDraw = false;
    }

    onPlayerResultUpdated($event) {
        console.log($event);
        if ($event.isNone()) {
            this.resultByPlayerId.delete($event.playerId);
        } else {
            this.resultByPlayerId.set($event.playerId, $event);
            if ($event.isYes() && !this.isDraw) {
                // open combinations selector
                console.log('open combinations selector');
                this.combinationSelectPlayerId = $event.playerId;
                this.showCombinationSelect = true;
            }
        }
        console.log(this.resultByPlayerId);
    }

    onCombinationsSelected($event) {
        console.log($event);
        this.combinationsByPlayerId.set(this.combinationSelectPlayerId, $event);
        this.showCombinationSelect = false;
        this.combinationSelectPlayerId = null;
    }

    submit() {
        let comp = this;
        let winners = [];
        let losers = [];
        let scores = [];
        if (this.isDraw) {
            this.game.players.forEach((player: PlayerShort) => {
                const result = comp.getResultFor(player.id);
                scores.push(PlayerScoreDto.fromTempai(player.id, result.isYes(), result.hasRiichi))
            });
        } else {
            this.game.players.forEach((player: PlayerShort) => {
                if (!comp.resultByPlayerId.has(player.id)) {
                    comp.resultByPlayerId.set(
                        player.id,
                        new PlayerResult(player.id, PlayerResult.RESULT_NONE, false)
                    );
                }
            });
            this.resultByPlayerId.forEach((result: PlayerResult) => {
                if (result.isYes()) {
                    winners.push(result.playerId);
                } else if (result.isNo()) {
                    losers.push(result.playerId);
                } else if (this.isTsumo && result.isNone()) {
                    losers.push(result.playerId);
                }
            });
            this.game.players.forEach((player: PlayerShort) => {
                const result = comp.getResultFor(player.id);
                if (result.isNo() || result.isNone()) {
                    scores.push(PlayerScoreDto.fromRiichi(player.id, result.hasRiichi));
                } else {
                    const playerCombinations = comp.getCombinationsFor(player.id);
                    scores.push(PlayerScoreDto.fromWin(player.id,
                        playerCombinations.combinations,
                        result.hasRiichi,
                        playerCombinations.doraCount,
                        playerCombinations.fuCount,
                        playerCombinations.openHand
                    ));
                }
            });
        }

        let roundScore = RoundScoreDto.from(scores, winners, losers);
        this.gameService.roundComplete(this.game.id, roundScore).subscribe(
            game => this.reset(game)
        );
    }

    private getResultFor(playerId: number): PlayerResult {
        if (this.resultByPlayerId.has(playerId)) {
            return this.resultByPlayerId.get(playerId);
        }
        return new PlayerResult(playerId, PlayerResult.RESULT_NONE, false);
    }

    private getCombinationsFor(playerId: number): PlayerCombinations {
        if (this.combinationsByPlayerId.has(playerId)) {
            return this.combinationsByPlayerId.get(playerId);
        }
        return new PlayerCombinations([], 0, 0, false);
    }
}
