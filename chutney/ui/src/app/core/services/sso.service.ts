/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { HttpClient } from '@angular/common/http';
import { AuthConfig, OAuthService } from 'angular-oauth2-oidc';
import { environment } from '@env/environment';
import { BehaviorSubject, map, tap } from 'rxjs';
import { Injectable } from '@angular/core';
import { filter, switchMap } from 'rxjs/operators';

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
  private enableSso = false


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
        tap(ssoConfig => this.oauthService.configure(ssoConfig)),
          switchMap(() => this.oauthService.loadDiscoveryDocumentAndTryLogin()),
          tap(res => this.enableSso = res),
          filter(() => this.oauthService.hasValidAccessToken() ),
          tap(() =>  this.tokenLoadedSubject.next(true) )
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
    return this.ssoConfig?.ssoProviderName
  }

  getSsoProviderImageUrl() {
      return this.ssoConfig?.ssoProviderImageUrl
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

  get getEnableSso() {
      return this.enableSso
  }
}
