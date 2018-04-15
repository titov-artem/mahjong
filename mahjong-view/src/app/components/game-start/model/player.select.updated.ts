import {PlayerShort} from '../../../model/player.short';

export class PlayerSelectUpdated {
    constructor(public player: PlayerShort,
                public wind: string,
                public prevPlayer: PlayerShort,
                public prevWind: string) {
    }
}