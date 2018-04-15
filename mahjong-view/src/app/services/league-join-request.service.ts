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
import {JoinRequestForm} from '../model/league/join-request-form';
import {JoinRequestView} from '../model/league/join-request-view';
import {JoinRequestRejectForm} from '../model/league/join-request-reject-form';
import {Globals} from '../globals';

@Injectable()
export class LeagueJoinRequestService extends AbstractHttpService {

    private httpOptions = {
        headers: new HttpHeaders({'Content-Type': 'application/json'})
    };
    private joinRequestUrl = '/api/league/join/request';

    constructor(http: HttpClient, cookieService: CookieService, router: Router, globals: Globals) {
        super('LeagueJoinRequestService', http, cookieService, router, globals);
    }

    getOutgoing(): Observable<JoinRequestView[]> {
        const url = `${this.joinRequestUrl}/outgoing`;
        return this.http.get<JoinRequestView[]>(url, this.httpOptions).pipe(
            tap(_ => this.log('get outgoing join requests')),
            catchError(this.handleHttpError<JoinRequestView[]>('get outgoing join request')),
        );
    }

    getIngoing(leagueId: number): Observable<JoinRequestView[]> {
        const url = `${this.joinRequestUrl}/league/${leagueId}/ingoing`;
        return this.http.get<JoinRequestView[]>(url, this.httpOptions).pipe(
            tap(_ => this.log('get ingoing join requests')),
            catchError(this.handleHttpError<JoinRequestView[]>('get ingoing join request')),
        );
    }

    create(form: JoinRequestForm): Observable<JoinRequestView> {
        console.log(form);
        return this.http.post<JoinRequestView>(this.joinRequestUrl, form, this.httpOptions).pipe(
            tap(_ => this.log('join request create')),
            catchError(this.handleHttpError<JoinRequestView>('join request create'))
        )
    }

    approve(id: number, onSuccess: () => void) {
        const url = `${this.joinRequestUrl}/${id}/approve`;
        return this.http.post<JoinRequestView>(url, '', this.httpOptions).pipe(
            tap(_ => this.log('join request approve')),
            catchError(this.handleHttpError<JoinRequestView>('join request approve'))
        ).subscribe(onSuccess);
    }

    reject(id: number, form: JoinRequestRejectForm, onSuccess: () => void) {
        const url = `${this.joinRequestUrl}/${id}/reject`;
        this.http.post<JoinRequestView>(url, form, this.httpOptions).pipe(
            tap(_ => this.log('join request reject')),
            catchError(this.handleHttpError<JoinRequestView>('join request reject'))
        ).subscribe(onSuccess);
    }
}