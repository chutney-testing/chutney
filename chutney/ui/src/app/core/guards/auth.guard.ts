/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, RouterStateSnapshot } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

import { LoginService } from '@core/services';
import { AlertService } from '@shared';
import {Authorization, User} from '@model';
import {OAuthService} from "angular-oauth2-oidc";
import {SsoOpenIdConnectService} from "@core/services/sso-open-id-connect.service";
import {HttpHeaders} from "@angular/common/http";
import {firstValueFrom} from "rxjs";


export const authGuard: CanActivateFn = async (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
    const translateService = inject(TranslateService);
    const loginService = inject(LoginService);
    const alertService = inject(AlertService);
    const ssoOpenIdConnectService = inject(SsoOpenIdConnectService);

    const requestURL = state.url !== undefined ? state.url : '';
    const unauthorizedMessage = translateService.instant('login.unauthorized')

    if (!loginService.isAuthenticated()) {
        if (ssoOpenIdConnectService.token) {
            console.log(ssoOpenIdConnectService.token)
            const user: User = await firstValueFrom(loginService.currentUser(true, {
                'Authorization': 'Bearer ' + ssoOpenIdConnectService.token
            }))
            if (user) {
                console.log('-------------------')
                console.log(user)
                return true
            }
        }
        loginService.initLogin(requestURL);
        return false;
    }

    const authorizations: Array<Authorization> = route.data['authorizations'] || [];
    if (loginService.hasAuthorization(authorizations)) {
        return true;
    } else {
        alertService.error(unauthorizedMessage, {timeOut: 0, extendedTimeOut: 0, closeButton: true});
        loginService.navigateAfterLogin();
        return false;
    }
}
