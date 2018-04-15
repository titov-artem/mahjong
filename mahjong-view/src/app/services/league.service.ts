import {AbstractHttpService} from './abstract.http.service';
import {Observable} from 'rxjs/Observable';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, tap} from 'rxjs/operators';
import {CookieService} from 'ngx-cookie-service';
import {Router} from '@angular/router';
import {Injectable} from '@angular/core';
import {LeagueView} from '../model/league/league-view';
import {LeagueForm} from '../model/league/league-form';
import {GameView} from '../model/game.view';
import {Globals} from '../globals';

@Injectable()
export class LeagueService extends AbstractHttpService {

    private httpOptions = {
        headers: new HttpHeaders({'Content-Type': 'application/json'})
    };
    private leagueUrl = '/api/league/league';

    constructor(http: HttpClient, cookieService: CookieService, router: Router, globals: Globals) {
        super('LeagueService', http, cookieService, router, globals);
    }

    getLeague(id: number): Observable<LeagueView> {
        const url = `${this.leagueUrl}/${id}`;
        return this.http.get<LeagueView>(url, this.httpOptions).pipe(
            tap(_ => this.log('get league')),
            catchError(this.handleHttpError<LeagueView>('get league')),
        );
    }

    getLeagues(): Observable<LeagueView[]> {
        return this.http.get<LeagueView[]>(this.leagueUrl, this.httpOptions).pipe(
            tap(_ => this.log('get leagues')),
            catchError(this.handleHttpError<LeagueView[]>('get leagues')),
        );
    }

    getJoinedLeagues(): Observable<LeagueView[]> {
        const url = `${this.leagueUrl}/joined`;
        return this.http.get<LeagueView[]>(url, this.httpOptions).pipe(
            tap(_ => this.log('get leagues')),
            catchError(this.handleHttpError<LeagueView[]>('get joined leagues')),
        );
    }

    getAdminedLeagues(): Observable<LeagueView[]> {
        const url = `${this.leagueUrl}/admin`;
        return this.http.get<LeagueView[]>(url, this.httpOptions).pipe(
            tap(_ => this.log('get admin leagues')),
            catchError(this.handleHttpError<LeagueView[]>('get admin leagues')),
        );
    }

    create(form: LeagueForm): Observable<LeagueView> {
        return this.http.post<LeagueView>(this.leagueUrl, form, this.httpOptions).pipe(
            tap(_ => this.log('league create')),
            catchError(this.handleHttpError<LeagueView>('league create'))
        )
    }
}