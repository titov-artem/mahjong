import {Wind} from './wind';

export class GameStartForm {
    players: number[];
    windToPlayer: Map<Wind, number>;
    rulesSet: string;
    windsToPlay: Wind[];
    startPoints: number;
    withUma: boolean;

    static withSettings(rulesSet: string,
                        windsToPlay: Wind[],
                        startPoints: number,
                        withUma: boolean): GameStartForm {
        let form = new GameStartForm();
        form.rulesSet = rulesSet;
        form.windsToPlay = windsToPlay;
        form.startPoints = startPoints;
        form.withUma = withUma;
        return form;
    }
}