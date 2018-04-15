import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {AbstractHttpService} from './abstract.http.service';
import {Player} from '../model/player';
import {Observable} from 'rxjs/Observable';
import {catchError, tap} from 'rxjs/operators';
import {CookieService} from 'ngx-cookie-service';
import {Router} from '@angular/router';
import {PlayerShort} from '../model/player.short';
import {Globals} from '../globals';

@Injectable()
export class PlayerService extends AbstractHttpService {

    private httpOptions = {
        headers: new HttpHeaders({'Content-Type': 'application/json'})
    };
    private playersUrl = '/api/main/player';
    private leaguePlayersUrl = '/api/league/player';

    constructor(http: HttpClient, cookieService: CookieService, router: Router, globals: Globals) {
        super('PlayerService', http, cookieService, router, globals);
    }

    getCurrent(): Observable<Player> {
        const url = `${this.playersUrl}/current`;
        return this.http.get<Player>(url, this.httpOptions)
            .pipe(
                tap(_ => this.log('getCurrent')),
                catchError(this.handleHttpError<Player>('getCurrent'))
            )
    }

    getPlayers(): Observable<PlayerShort[]> {
        return this.http.get<PlayerShort[]>(this.playersUrl, this.httpOptions)
            .pipe(
                tap(_ => this.log('getPlayers')),
                catchError(this.handleHttpError<PlayerShort[]>('getPlayers'))
            )
    }

    getLeaguePlayers(leagueId: number): Observable<PlayerShort[]> {
        const url = `${this.leaguePlayersUrl}?leagueId=${leagueId}`;
        return this.http.get<PlayerShort[]>(url, this.httpOptions)
            .pipe(
                tap(_ => this.log('getLeaguePlayers')),
                catchError(this.handleHttpError<PlayerShort[]>('getLeaguePlayers'))
            )
    }

    createPlayer(player: Player): Observable<Player> {
        return this.http.post<Player>(this.playersUrl, player, this.httpOptions)
            .pipe(
                tap(player => this.log('createPlayer')),
                catchError(this.handleHttpError<Player>('createPlayer'))
            )
    }

    updatePlayer(player: Player): Observable<Player> {
        const url = `${this.playersUrl}/${player.id}`;
        return this.http.put<Player>(url, player, this.httpOptions)
            .pipe(
                tap(player => this.log('updatePlayer')),
                catchError(this.handleHttpError<Player>('updatePlayer'))
            )
    }
}