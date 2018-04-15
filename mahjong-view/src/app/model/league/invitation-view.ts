import {PlayerShort} from '../player.short';

export class InvitationView {

    leagueId: number;
    leagueName: string;
    player: PlayerShort;
    code: string;
    author: PlayerShort;
    createdAt: Date;
    expiredAt: Date;

}
