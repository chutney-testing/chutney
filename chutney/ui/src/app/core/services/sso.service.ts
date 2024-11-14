/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { HttpClient, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { AuthConfig, OAuthService } from 'angular-oauth2-oidc';
import { environment } from '@env/environment';
import { BehaviorSubject, map, Observable, tap } from 'rxjs';
import { Injectable } from '@angular/core';
import { filter } from 'rxjs/operators';

interface SsoAuthConfig {
  issuer: string,
  clientId: string,
  clientSecret: string,
  responseType: string,
  scope: string,
  redirectBaseUrl: string,
  ssoProviderName: string,
  ssoProviderImageUrl: string,
  headers: { [name: string]: string | string[]; },
  additionalQueryParams: { [name: string]: string | string[]; }
  oidc: boolean
}

@Injectable({
  providedIn: 'root'
})
export class SsoService {

  private resourceUrl = '/api/v1/sso/config';

  private ssoConfig: SsoAuthConfig

  private tokenLoadedSubject = new BehaviorSubject<boolean>(false);
  public tokenLoaded$ = this.tokenLoadedSubject.asObservable();


  constructor(private oauthService: OAuthService, private http: HttpClient) {
      this.oauthService.events
          .pipe(filter(e => e.type === 'token_received'))
          .subscribe(() => {
              this.tokenLoadedSubject.next(true);
          });
  }

  fetchSsoConfig(): void {
    this.http.get<SsoAuthConfig>(environment.backend + this.resourceUrl).pipe(
        map(ssoConfig => {
          this.ssoConfig = ssoConfig
          return {
            issuer: ssoConfig.issuer,
            clientId: ssoConfig.clientId,
            responseType: ssoConfig.responseType,
            scope: ssoConfig.scope,
            redirectUri: ssoConfig.redirectBaseUrl + '/',
            dummyClientSecret: ssoConfig.clientSecret,
            oidc: ssoConfig.oidc,
            useHttpBasicAuth: true,
            postLogoutRedirectUri: ssoConfig.redirectBaseUrl + '/',
            sessionChecksEnabled: true,
            logoutUrl: ssoConfig.redirectBaseUrl + '/',
            customQueryParams: ssoConfig.additionalQueryParams,
            useIdTokenHintForSilentRefresh: true,
            redirectUriAsPostLogoutRedirectUriFallback: true,
          } as AuthConfig
        }),
        tap(async ssoConfig => {
            try {
                this.oauthService.configure(ssoConfig)
                await this.oauthService.loadDiscoveryDocumentAndTryLogin();
                if (this.oauthService.hasValidAccessToken()) {
                    this.tokenLoadedSubject.next(true);
                }
            } catch (e) {
                console.error("SSO provider not available")
            }
        })
    ).subscribe()
  }

  login() {
      this.oauthService.initCodeFlow();
  }

  logout() {
      if (this.idToken) {
          this.oauthService.logOut({
              'id_token_hint': this.idToken
          });
      }
  }

  getSsoProviderName() {
    if (this.ssoConfig) {
      return this.ssoConfig.ssoProviderName
    }
    return null
  }

  getSsoProviderImageUrl() {
    if (this.ssoConfig) {
      return this.ssoConfig.ssoProviderImageUrl
    }
    return null
  }

  get accessToken(): string {
      return this.oauthService.getAccessToken();
  }

  get idToken(): string {
      return this.oauthService.getIdToken();
  }

  get tokenEndpoint(): string {
      return this.oauthService.tokenEndpoint;
  }

  get headers() {
      return this.ssoConfig?.headers
  }
}

@Injectable()
export class OAuth2ContentTypeInterceptor implements HttpInterceptor {

    constructor(private ssoService: SsoService) {}

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const isTokenEndpoint = this.ssoService.headers && req.url.startsWith(this.ssoService.tokenEndpoint);
        if (isTokenEndpoint) {
            const modifiedReq = req.clone({
                setHeaders: this.ssoService.headers
            });
            return next.handle(modifiedReq);
        }
        return next.handle(req);
    }
}
