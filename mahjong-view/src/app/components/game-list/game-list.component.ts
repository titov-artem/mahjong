import {Component, OnInit} from '@angular/core';
import {GameService} from '../../services/game.service';
import {GameView} from '../../model/game.view';
import {Wind} from '../../model/wind';
import {Router} from '@angular/router';

@Component({
    selector: 'game-list',
    templateUrl: './game-list.component.html',
    styleUrls: ['./game-list.component.css']
})
export class GameListComponent implements OnInit {

    games: GameView[];

    constructor(private router: Router,
                private gameService: GameService) {
    }

    ngOnInit() {
        this.getActiveGames();
    }

    private getActiveGames() {
        this.gameService.getActive().subscribe(games => this.games = games);
    }

    getPlayerOnEast(game: GameView): string {
        return this.getPlayerOn(game, Wind.EAST);
    }

    getPlayerOnSouth(game: GameView): string {
        return this.getPlayerOn(game, Wind.SOUTH);
    }

    getPlayerOnWest(game: GameView): string {
        return this.getPlayerOn(game, Wind.WEST);
    }

    getPlayerOnNorth(game: GameView): string {
        return this.getPlayerOn(game, Wind.NORTH);
    }

    private getPlayerOn(game: GameView, wind: Wind): string {
        return game.players.get(wind).name;
    }

    getScoreOnEast(game: GameView): number {
        return this.getScore(game, Wind.EAST);
    }

    getScoreOnSouth(game: GameView): number {
        return this.getScore(game, Wind.SOUTH);
    }

    getScoreOnWest(game: GameView): number {
        return this.getScore(game, Wind.WEST);
    }

    getScoreOnNorth(game: GameView): number {
        return this.getScore(game, Wind.NORTH);
    }

    private getScore(game: GameView, wind: Wind): number {
        return game.scores.get(wind);
    }

    openGame(game: GameView) {
        this.router.navigate(['/games/' + game.id]);
    }

}
