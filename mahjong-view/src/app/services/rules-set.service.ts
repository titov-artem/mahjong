import {AbstractHttpService} from './abstract.http.service';
import {Observable} from 'rxjs/Observable';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, tap} from 'rxjs/operators';
import {CookieService} from 'ngx-cookie-service';
import {Router} from '@angular/router';
import {Injectable} from '@angular/core';
import {CombinationView} from '../model/combination-view';
import {RulesSetView} from '../model/rules-set-view';
import {Globals} from '../globals';

@Injectable()
export class RulesSetService extends AbstractHttpService {

    private httpOptions = {
        headers: new HttpHeaders({'Content-Type': 'application/json'})
    };
    private rulesUrl = '/api/main/rules';

    constructor(http: HttpClient, cookieService: CookieService, router: Router, globals: Globals) {
        super('RulesSetService', http, cookieService, router, globals);
    }

    get(rulesSetCode: string): Observable<RulesSetView> {
        const url = `${this.rulesUrl}/${rulesSetCode}`;
        return this.http.get<RulesSetView>(url, this.httpOptions).pipe(
            tap(_ => this.log('get rules set')),
            catchError(this.handleHttpError<RulesSetView>('get rules set'))
        );
    }

    getCombinations(rulesSetCode: string): Observable<CombinationView[]> {
        const url = `${this.rulesUrl}/combinations?rules=${rulesSetCode}`;
        return this.http.get<CombinationView[]>(url, this.httpOptions).pipe(
            tap(_ => this.log('Get combinations for ' + this.rulesUrl)),
            catchError(this.handleHttpError<CombinationView[]>('get combination')),
        );
    }
}