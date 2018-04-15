import {AbstractHttpService} from './abstract.http.service';
import {Observable} from 'rxjs/Observable';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, tap} from 'rxjs/operators';
import {CookieService} from 'ngx-cookie-service';
import {Router} from '@angular/router';
import {Injectable} from '@angular/core';
import {LeagueView} from '../model/league/league-view';
import {Globals} from '../globals';

@Injectable()
export class EnumsService extends AbstractHttpService {

    private httpOptions = {
        headers: new HttpHeaders({'Content-Type': 'application/json'})
    };
    private enumsUrl = '/api/main/enums';

    constructor(http: HttpClient, cookieService: CookieService, router: Router, globals: Globals) {
        super('EnumsService', http, cookieService, router, globals);
    }

    getSupportedLangs(): Observable<string[]> {
        const url = `${this.enumsUrl}/langs`;
        return this.http.get<string[]>(url, this.httpOptions).pipe(
            tap(_ => this.log('get langs')),
            catchError(this.handleHttpError<string[]>('get langs')),
        );
    }

}