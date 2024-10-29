/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { HttpClient } from '@angular/common/http';
import { OAuthService } from 'angular-oauth2-oidc';
import { environment } from '@env/environment';
import { map, tap } from 'rxjs';
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
            oidc: ssoConfig.oidc
          }
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
      return this.oauthService.getAccessToken();
  }
}
