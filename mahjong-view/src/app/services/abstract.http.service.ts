import {Observable} from 'rxjs/Observable';
import {CookieService} from 'ngx-cookie-service';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../globals';
import {ErrorView} from '../model/error-view';

export abstract class AbstractHttpService {

    constructor(protected serviceName: string,
                protected http: HttpClient,
                protected cookieService: CookieService,
                protected router: Router,
                protected globals: Globals) {
    };

    /**
     * Handle Http operation that failed.
     * Let the app continue.
     * @param operation - name of the operation that failed
     * @param result - optional value to return as the observable result
     */
    protected handleHttpError<T>(operation = 'operation', result?: T) {
        return (error: any): Observable<T> => {

            this.log(`${operation} failed: ${error.message}`);
            this.logError(error);

            if (error.status == 403) {
                console.log('Access denied! Clearing cookie and redirecting to login page');
                this.clearCookie();
                this.router.navigate(['/signin']);
            } else {
                if (error.error != null && error.error.hasOwnProperty('message')) {
                    this.globals.emitError(error.error);
                } else {
                    const errorView = new ErrorView();
                    errorView.cls = 'UNKNOWN';
                    errorView.message = error.status + ': ' + error.statusText;
                    this.globals.emitError(errorView);
                }
            }

            throw error;
        };
    }

    protected log(message: string) {
        console.log(this.serviceName + ': ' + message);
    }

    protected logError(error: any) {
        console.error(error);
    }

    protected clearCookie() {
        this.cookieService.delete('JSESSIONID');
        this.cookieService.delete('login');
        this.cookieService.delete('data');
    }

}