import {Component, Input, OnInit} from '@angular/core';
import {PlayerService} from '../../services/player.service';
import {PlayerShort} from '../../model/player.short';
import {Wind, WindHelper} from '../../model/wind';
import {GameSeating} from './model/game.seating';
import {GameStartForm} from '../../model/game.start.form';
import {GameService} from '../../services/game.service';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
    selector: 'game-start',
    templateUrl: './game-start.component.html',
    styleUrls: ['./game-start.component.css']
})
export class GameStartComponent implements OnInit {

    private WIND_RANDOM = 'RANDOM';
    private PLAYER_RANDOM = 'Random';

    players: PlayerShort[] = [];
    playersById: Map<number, PlayerShort> = null;
    private gameSeating: GameSeating = new GameSeating();

    leagueId: number = null;

    constructor(private route: ActivatedRoute,
                private router: Router,
                private playerService: PlayerService,
                private gameService: GameService) {
    }

    ngOnInit() {
        if (this.route.snapshot.paramMap.has('leagueId')) {
            this.leagueId = +this.route.snapshot.paramMap.get('leagueId');
        }
        this.getPlayers();
    }

    private player(id: number, name: string): PlayerShort {
        let out = new PlayerShort();
        out.id = id;
        out.name = name;
        return out;
    }

    private getPlayers(): void {
        // todo understand why extraction of common code into calback fail
        if (this.leagueId != null) {
            this.playerService.getLeaguePlayers(this.leagueId)
                .subscribe(players => {
                    this.players = players;
                    this.playersById = new Map<number, PlayerShort>();
                    for (let p of this.players) {
                        this.playersById.set(p.id, p);
                    }
                });
        } else {
            this.playerService.getPlayers()
                .subscribe(players => {
                    this.players = players;
                    this.playersById = new Map<number, PlayerShort>();
                    for (let p of this.players) {
                        this.playersById.set(p.id, p);
                    }
                })
        }
    }

    playerSelectUpdated($event) {
        this.gameSeating.update($event.player, $event.wind, $event.prevPlayer, $event.prevWind);
    }

    getPlayerOnEast(): string {
        return this.getPlayerForWind(Wind.EAST);
    }

    getPlayerOnSouth(): string {
        return this.getPlayerForWind(Wind.SOUTH);
    }

    getPlayerOnWest(): string {
        return this.getPlayerForWind(Wind.WEST);
    }

    getPlayerOnNorth(): string {
        return this.getPlayerForWind(Wind.NORTH);
    }

    private getPlayerForWind(w: Wind): string {
        let player = this.gameSeating.getPlayerOn(w);
        if (player == null) {
            return null;
        }
        return player.name;
    }

    hasError() {
        return this.gameSeating.hasError();
    }

    getErrorMessage() {
        return this.gameSeating.getErrorMessage();
    }

    start() {
        if (!this.gameSeating.validate()) {
            return;
        }
        console.log('Players seating valid');
        let form = GameStartForm.withSettings('RIICHI_EMA', [Wind.EAST, Wind.SOUTH], 30000, false);
        this.gameSeating.fillGameStartForm(form);
        console.log(form);
        if (this.leagueId != null) {
            this.gameService.startInLeague(this.leagueId, form).subscribe(
                game => this.router.navigate([`/games/${game.id}`]),
                error => console.log(error)
            );
        } else {
            console.log("Currently can start game only in league");
        }
    }

}
