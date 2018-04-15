export class InvitationForm {

    leagueId: number;
    playerId: number;

    constructor(leagueId: number, playerId: number) {
        this.leagueId = leagueId;
        this.playerId = playerId;
    }
}
