import {Wind} from './wind';
import {PlayerShort} from './player.short';

export class GameView {
    id: number;
    rulesSetCode: string;
    /**
     * Initial wind to player
     */
    players: Map<Wind, PlayerShort>;
    /**
     * Initial wind to player's score
     */
    scores: Map<Wind, number>;
    /**
     * Initial wind to player's current wind
     */
    windMapping: Map<Wind, Wind>;
    riichiSticksOnTable: number;
    honbaCount: number;
    currentDealer: number;
    currentWind: Wind;
    currentWindRoundNumber: number;

    windsToPlay: Set<Wind>;
    startPoints: number;
    withUma: boolean;
}