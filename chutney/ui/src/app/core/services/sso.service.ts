/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { HttpClient, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { AuthConfig, OAuthService } from 'angular-oauth2-oidc';
import { environment } from '@env/environment';
import { map, Observable, tap } from 'rxjs';
import { Injectable } from '@angular/core';

interface SsoAuthConfig {
  issuer: string,
  clientId: string,
  clientSecret: string,
  responseType: string,
  scope: string,
  redirectBaseUrl: string,
  ssoProviderName: string,
  ssoProviderImageUrl: string,
  uriRequireHeader: string,
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


  constructor(private oauthService: OAuthService, private http: HttpClient) {}

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
            postLogoutRedirectUri: ssoConfig.redirectBaseUrl,
            sessionChecksEnabled: true,
            logoutUrl: ssoConfig.redirectBaseUrl,
            customQueryParams: ssoConfig.additionalQueryParams,
            useIdTokenHintForSilentRefresh: true
          } as AuthConfig
        }),
        tap(async ssoConfig => {
            try {
                this.oauthService.configure(ssoConfig)
                await this.oauthService.loadDiscoveryDocumentAndTryLogin();
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
      this.oauthService.logOut();
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

  get uriRequireHeader() {
      return this.ssoConfig?.uriRequireHeader
  }

  get headers() {
      return this.ssoConfig?.headers
  }
}

@Injectable()
export class OAuth2ContentTypeInterceptor implements HttpInterceptor {

    constructor(private ssoService: SsoService) {}

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const isOAuth2Service = this.ssoService.uriRequireHeader && req.url.includes(this.ssoService.uriRequireHeader);
        if (isOAuth2Service) {
            const modifiedReq = req.clone({
                setHeaders: this.ssoService.headers
            });
            return next.handle(modifiedReq);
        }
        const isEndSessionUri = req.url.includes('oauth2/multiauth/connect/endSession');
        if (isEndSessionUri) {
            console.log('TOTOTOTOOTOTTOTOT')
            const modifiedReq = req.clone({
                setParams: {'id_token_hint': this.ssoService.idToken}
            });
            return next.handle(modifiedReq);
        }
        return next.handle(req);
    }
}
