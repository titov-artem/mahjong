import {EventEmitter, Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {AbstractHttpService} from './abstract.http.service';
import {Observable} from 'rxjs/Observable';
import {catchError, tap} from 'rxjs/operators';
import {CookieService} from 'ngx-cookie-service';
import {Router} from '@angular/router';

import 'rxjs/Rx';
import {Globals} from '../globals';

const ctApplicationJsonHeaders = new HttpHeaders({'Content-Type': 'application/json'});
const ctTextPlainHeaders = new HttpHeaders({'Content-type': 'application/json'});
const ctFormUrlEncodedHeaders = new HttpHeaders({'Content-Type': 'application/x-www-form-urlencoded'});

@Injectable()
export class AuthService extends AbstractHttpService {

    public userAuthenticated: EventEmitter<boolean>;

    private authUrl = '/api/auth/user';

    constructor(http: HttpClient, cookieService: CookieService, router: Router, globals: Globals) {
        super('AuthService', http, cookieService, router, globals);
        this.userAuthenticated = new EventEmitter();
    }

    signUp(login: string, password: string): Observable<Object> {
        const url = `${this.authUrl}/create`;
        let user = {
            login: login,
            password: password
        };
        return this.http.post(url, user, {headers: ctApplicationJsonHeaders}).pipe(
            tap(_ => this.log('Sign up')),
            catchError(this.handleHttpError<Object>('Sign up failed')),
            tap(_ => this.clearCookie())
        )
    }

    /**
     * Authenticate user. If success emit event and call onSuccess callback
     * @param {string} login
     * @param {string} password
     * @param {() => void} onSuccess
     */
    signIn(login: string, password: string, onSuccess: () => void) {
        const body = new HttpParams()
            .set('login', login)
            .set('password', password);
        const url = `${this.authUrl}/authenticate`;
        this.http.post(url, body.toString(), {headers: ctFormUrlEncodedHeaders})
            .pipe(
                tap(_ => this.log('Sign in')),
                catchError(this.handleHttpError<Object>('Sign in failed'))
            )
            .subscribe(_ => {
                onSuccess();
                this.userAuthenticated.emit(true);
            });
    }

    current(): Observable<string> {
        console.log('Lading current user');
        const url = `${this.authUrl}/current`;
        return this.http.get(url, {headers: ctTextPlainHeaders, responseType: 'text'})
            .pipe(
                tap(user => this.log('Loaded current user: ' + user)),
                catchError(this.handleHttpError<string>('Get current user failed'))
            );
    }

    signOut() {
        const url = `${this.authUrl}/logout`;
        this.http.post(url, '', {headers: ctApplicationJsonHeaders}).pipe(
            tap(_ => this.log('Sign out')),
            catchError(this.handleHttpError<Object>('Sign out failed')),
        ).subscribe(
            _ => {
                console.log('Sign out success');
                this.userAuthenticated.emit(false);
                this.clearCookie();
                this.router.navigate(['/signin']);
            },
            err => {
                console.error('Failed to sign out');
                console.error(err);
            }
        );
    }
}