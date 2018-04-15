import {AbstractHttpService} from './abstract.http.service';
import {Observable} from 'rxjs/Observable';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, tap} from 'rxjs/operators';
import {CookieService} from 'ngx-cookie-service';
import {Router} from '@angular/router';
import {Injectable} from '@angular/core';
import {InvitationView} from '../model/league/invitation-view';
import {InvitationForm} from '../model/league/invitation-form';
import {Globals} from '../globals';

@Injectable()
export class LeagueInvitationService extends AbstractHttpService {

    private httpOptions = {
        headers: new HttpHeaders({'Content-Type': 'application/json'})
    };
    private invitationUrl = '/api/league/invitation';

    constructor(http: HttpClient, cookieService: CookieService, router: Router, globals: Globals) {
        super('LeagueInvitationService', http, cookieService, router, globals);
    }

    getInvitations(): Observable<InvitationView[]> {
        return this.http.get<InvitationView[]>(this.invitationUrl, this.httpOptions).pipe(
            tap(_ => this.log('get invitation')),
            catchError(this.handleHttpError<InvitationView[]>('get invitation')),
        );
    }

    create(form: InvitationForm): Observable<InvitationView> {
        return this.http.post<InvitationView>(this.invitationUrl, form, this.httpOptions).pipe(
            tap(_ => this.log('invitation create')),
            catchError(this.handleHttpError<InvitationView>('invitation create'))
        )
    }

    accept(code: string, onSuccess: () => void) {
        const url = `${this.invitationUrl}/accept`;
        this.http.post<InvitationView>(url, code, this.httpOptions).pipe(
            tap(_ => this.log('invitation create')),
            catchError(this.handleHttpError<InvitationView>('invitation create'))
        ).subscribe(onSuccess);
    }

    reject(code: string, onSuccess: () => void) {
        const url = `${this.invitationUrl}/reject`;
        this.http.post<InvitationView>(url, code, this.httpOptions).pipe(
            tap(_ => this.log('invitation create')),
            catchError(this.handleHttpError<InvitationView>('invitation create'))
        ).subscribe(onSuccess);
    }
}