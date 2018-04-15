import {AbstractHttpService} from './abstract.http.service';
import {GameStartForm} from '../model/game.start.form';
import {Observable} from 'rxjs/Observable';
import {GameView} from '../model/game.view';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, tap} from 'rxjs/operators';
import {CookieService} from 'ngx-cookie-service';
import {Router} from '@angular/router';
import {Injectable} from '@angular/core';
import {PlayerShort} from '../model/player.short';
import {Wind} from '../model/wind';
import {RoundScoreDto} from '../model/round-score-dto';
import {Globals} from '../globals';

@Injectable()
export class GameService extends AbstractHttpService {

    private httpOptions = {
        headers: new HttpHeaders({'Content-Type': 'application/json'})
    };
    private gameUrl = '/api/main/game';
    private leagueGameUrl = '/api/league/game';

    constructor(http: HttpClient, cookieService: CookieService, router: Router, globals: Globals) {
        super('GameService', http, cookieService, router, globals);
    }

    startInLeague(leagueId: number, form: GameStartForm): Observable<GameView> {
        const url = `${this.leagueGameUrl}/league/${leagueId}`;
        return this.http.post<GameView>(url, form, this.httpOptions).pipe(
            tap(_ => this.log('Game startInLeague request')),
            catchError(this.handleHttpError<GameView>('startInLeague game'))
        )
    }

    getActive(): Observable<GameView[]> {
        const url = `${this.gameUrl}/active`;
        return this.http.get<GameView[]>(url, this.httpOptions).pipe(
            tap(_ => this.log('Get active games')),
            catchError(this.handleHttpError<GameView[]>('get active games')),
        ).map(games => {
            games.forEach(this.fixGameView);
            return games;
        })
    }

    get(id: number): Observable<GameView> {
        const url = `${this.gameUrl}/${id}`;
        return this.http.get<GameView>(url, this.httpOptions).pipe(
            tap(_ => this.log('Get game')),
            catchError(this.handleHttpError<GameView>('get game')),
        ).map(this.fixGameView)
    }

    roundComplete(id: number, score: RoundScoreDto): Observable<GameView> {
        const url = `${this.gameUrl}/${id}`;
        return this.http.patch<GameView>(url, score, this.httpOptions).pipe(
            tap(_ => this.log('Round complete')),
            catchError(this.handleHttpError<GameView>('round complete')),
        ).map(this.fixGameView)
    }

    private fixGameView(game: GameView): GameView {
        // Convert players from object to map.
        let playersMap = new Map<Wind, PlayerShort>();
        Object.keys(game.players).forEach(key => {
            playersMap.set(Wind[key], game.players[key]);
        });
        game.players = playersMap;

        // Convert scores from object to map.
        let scoresMap = new Map<Wind, number>();
        Object.keys(game.scores).forEach(key => {
            scoresMap.set(Wind[key], game.scores[key]);
        });
        game.scores = scoresMap;

        // Convert windMapping from object to map.
        let windMapping = new Map<Wind, Wind>();
        Object.keys(game.windMapping).forEach(key => {
            const currentWindRaw: string = game.windMapping[key];
            windMapping.set(Wind[key], Wind[currentWindRaw]);
        });
        console.log(game.windMapping);
        return game;
    }

}