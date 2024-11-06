/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { BehaviorSubject, firstValueFrom, Observable } from 'rxjs';
import { catchError, delay, tap } from 'rxjs/operators';

import { environment } from '@env/environment';
import { Authorization, User } from '@model';
import { contains, intersection, isNullOrBlankString } from '@shared/tools';
import { SsoService } from '@core/services/sso.service';
import { TranslateService } from '@ngx-translate/core';
import { AlertService } from '@shared';

@Injectable({
    providedIn: 'root'
})
export class LoginService {

    private url = '/api/v1/user';
    private loginUrl = this.url + '/login';
    private NO_USER = new User('');
    private user$: BehaviorSubject<User> = new BehaviorSubject(this.NO_USER);

    constructor(
        private http: HttpClient,
        private router: Router,
        private ssoService: SsoService,
        private translateService: TranslateService,
        private alertService: AlertService
    ) {
    }

    async isAuthorized(requestURL: string, route: ActivatedRouteSnapshot) {
        const unauthorizedMessage = this.translateService.instant('login.unauthorized')
        if (!this.isAuthenticated()) {
            if (this.oauth2Token) {
                await firstValueFrom(this.initLoginObservable(requestURL, {
                    'Authorization': 'Bearer ' + this.oauth2Token
                }));
            } else {
                await this.initLogin(requestURL);
                return false;
            }
        }
        const authorizations: Array<Authorization> = route.data['authorizations'] || [];
        if (this.hasAuthorization(authorizations)) {
            return true;
        } else {
            this.alertService.error(unauthorizedMessage, {timeOut: 0, extendedTimeOut: 0, closeButton: true});
            this.navigateAfterLogin();
            return false;
        }
    }

    async initLogin(url?: string, headers: HttpHeaders | {
        [header: string]: string | string[];
    } = {}) {
        await firstValueFrom(this.initLoginObservable(url, headers))
    }

    initLoginObservable(url?: string, headers?: HttpHeaders | {
        [header: string]: string | string[];
    }) {
        return this.currentUser(true, headers).pipe(
            tap(user => this.setUser(user)),
            tap(_ => this.navigateAfterLogin(url)),
            catchError(error => {
                const nextUrl = this.nullifyLoginUrl(url);
                const queryParams: Object = isNullOrBlankString(nextUrl) ? {} : {queryParams: {url: nextUrl}};
                this.router.navigate(['login'], queryParams);
                return error
            })
        );
    }

    get oauth2Token(): string {
        return this.ssoService.accessToken
    }

    login(username: string, password: string): Observable<User> {
        if (isNullOrBlankString(username) && isNullOrBlankString(password)) {
            return this.currentUser().pipe(
                tap(user => this.setUser(user))
            );
        }

        const body = new URLSearchParams();
        body.set('username', username);
        body.set('password', password);

        const options = {
            headers: new HttpHeaders()
                .set('Content-Type', 'application/x-www-form-urlencoded')
                .set('no-intercept-error', '')
        };

        return this.http.post<User>(environment.backend + this.loginUrl, body.toString(), options)
            .pipe(
                tap(user => this.setUser(user))
            );
    }

    navigateAfterLogin(url?: string) {
        const nextUrl = this.nullifyLoginUrl(url);
        if (this.isAuthenticated()) {
            const user: User = this.user$.getValue();
            this.router.navigateByUrl(nextUrl ? nextUrl : this.defaultForwardUrl(user));
        } else {
            this.router.navigateByUrl('/login');
        }
    }

    logout() {
        this.http.post(environment.backend + this.url + '/logout', null).pipe(
            tap(() => this.setUser(this.NO_USER)),
            tap(() => this.ssoService.logout()),
            delay(500)
        ).subscribe(
            () => {
                this.router.navigateByUrl('/login');
            }
        );
    }

    getUser(): Observable<User> {
        return this.user$;
    }

    isAuthenticated(): boolean {
        const user: User = this.user$.getValue();
        return this.NO_USER !== user;
    }

    hasAuthorization(authorization: Array<Authorization> | Authorization = [], u: User = null): boolean {
        const user: User = u || this.user$.getValue();
        const auth = [].concat(authorization);
        if (user != this.NO_USER) {
            return auth.length == 0 || intersection(user.authorizations, auth).length > 0;
        }
        return false;
    }

    isLoginUrl(url: string): boolean {
        return url.includes(this.loginUrl);
    }

    currentUser(skipInterceptor: boolean = false, headers: HttpHeaders | {
        [header: string]: string | string[];
    } = {}): Observable<User> {
        const headersInterceptor = skipInterceptor ? {'no-intercept-error': ''} : {}
        const options = {
            headers: {...headersInterceptor, ...headers}
        };
        return this.http.get<User>(environment.backend + this.url, options);
    }

    private setUser(user: User) {
        this.user$.next(user);
    }

    private defaultForwardUrl(user: User): string {
        const authorizations = user.authorizations;
        if (authorizations) {
            if (contains(authorizations, Authorization.SCENARIO_READ)) return '/scenario';
            if (contains(authorizations, Authorization.CAMPAIGN_READ)) return '/campaign';
            if (contains(authorizations, Authorization.ENVIRONMENT_ACCESS)) return '/targets';
            if (contains(authorizations, Authorization.GLOBAL_VAR_READ)) return '/variable';
            if (contains(authorizations, Authorization.DATASET_READ)) return '/dataset';
            if (contains(authorizations, Authorization.ADMIN_ACCESS)) return '/';
        }

        return '/login';
    }

    private nullifyLoginUrl(url: string): string {
        return url && url !== '/login' ? url : null;
    }
}
