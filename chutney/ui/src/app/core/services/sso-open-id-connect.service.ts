import { HttpClient } from '@angular/common/http';
import { AuthConfig, OAuthService } from 'angular-oauth2-oidc';
import { environment } from '@env/environment';
import { Observable, map } from 'rxjs';
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
export class SsoOpenIdConnectService {

  private resourceUrl = '/api/v1/sso/config';

  private ssoConfig: SsoAuthConfig


  constructor(private oauthService: OAuthService, private http: HttpClient) {}

  fetchSsoConfig(): Observable<AuthConfig> {
      return this.http.get<SsoAuthConfig>(environment.backend + this.resourceUrl).pipe(
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
        })
      )
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

  get identityClaims() {
      return this.oauthService.getIdentityClaims();
  }

  get token(): string {
      return this.oauthService.getAccessToken();
  }

  get isLoggedIn() {
      return this.oauthService.hasValidAccessToken();
  }
}
