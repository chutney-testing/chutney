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

  get token(): string {
      return this.oauthService.getIdToken();
  }
}

@Injectable()
export class OAuth2ContentTypeInterceptor implements HttpInterceptor {
    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const isOAuth2Service = req.url.includes('/oauth2/multiauth/access_token');
        if (isOAuth2Service) {
            const modifiedReq = req.clone({
                setHeaders: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            });
            return next.handle(modifiedReq);
        }
        return next.handle(req);
    }
}
