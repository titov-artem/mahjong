import {Wind, WindHelper} from '../../../model/wind';
import {PlayerShort} from '../../../model/player.short';
import {GameStartForm} from '../../../model/game.start.form';

const ANY_PLAYER = -1;
const RANDOM_WIND = 'RANDOM';

export class GameSeating {

    private windToPlayers = new Map<string, PlayerShort[]>();
    private errorMessage: string = null;

    // return null if there is an error
    getPlayerOn(wind: Wind): PlayerShort {
        if (this.hasError()) {
            return null;
        }
        if (this.getPlayers(wind).length == 0) {
            return PlayerShort.of(ANY_PLAYER, 'Any');
        }
        return this.windToPlayers.get(wind)[0];
    }

    hasError(): boolean {
        return this.errorMessage != null;
    }

    getErrorMessage(): string {
        return this.errorMessage;
    }

    update(player: PlayerShort, wind: string, prevPlayer: PlayerShort, prevWind: string) {
        if (prevPlayer != null) {
            console.log('Removing value: ' + prevPlayer.id);
            // remove previous player info
            this.deletePlayer(prevWind, prevPlayer);
        }
        // store current player
        this.getPlayers(wind).push(player);

        // clear error
        this.errorMessage = null;

        let comp = this;
        this.windToPlayers.forEach((players: PlayerShort[], wind: string) => {
            if (wind == RANDOM_WIND) {
                return;
            }
            if (players.length > 1) {
                comp.errorMessage = 'To many players with wind ' + wind;
            }
        });
    }

    validate(): boolean {
        if (this.hasError()) {
            return false;
        }
        let players = new Set();
        this.windToPlayers.forEach((ps: PlayerShort[], wind: string) => {
            for (let p of ps) {
                players.add(p.id);
            }
        });
        if (players.size != 4) {
            this.errorMessage = 'Not enough players';
            return false;
        }
        return true;
    }

    fillGameStartForm(form: GameStartForm) {
        if (!this.validate()) {
            console.error('Game seating is invalid, can\'t fill form!');
            return;
        }
        let players = [];
        let windToPlayer = new Map<Wind, number>();
        this.windToPlayers.forEach((ps: PlayerShort[], wind: string) => {
            ps.map(p => p.id)
                .forEach(p => players.push(p));
            if (wind != RANDOM_WIND && ps.length > 0) {
                windToPlayer.set(Wind[wind], ps[0].id);
            }
        });
        form.players = players;
        form.windToPlayer = windToPlayer;
    }

    private getPlayers(wind: string): PlayerShort[] {
        if (!this.windToPlayers.has(wind)) {
            this.windToPlayers.set(wind, []);
        }
        return this.windToPlayers.get(wind);
    }

    private deletePlayer(wind: string, player: PlayerShort) {
        let players = this.getPlayers(wind);
        let index = players.indexOf(player, 0);
        if (index > -1) {
            players.splice(index, 1);
        }
    }

}