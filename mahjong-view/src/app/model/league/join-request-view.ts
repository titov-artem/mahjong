import {PlayerShort} from '../player.short';

export class JoinRequestView {

    id: number;
    leagueId: number;
    player: PlayerShort;
    decision: string;
    reason: string;
    admin: PlayerShort;
    consideredAt: Date;
    expireAt: Date;

}
