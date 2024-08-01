/**
 * Copyright 2017-2024 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, RouterStateSnapshot } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

import { LoginService } from '@core/services';
import { AlertService } from '@shared';
import { Authorization } from '@model';


export const authGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
    const translateService = inject(TranslateService);
    const loginService = inject(LoginService);
    const alertService = inject(AlertService);

    const requestURL = state.url !== undefined ? state.url : '';
    const unauthorizedMessage = translateService.instant('login.unauthorized')
    if (!loginService.isAuthenticated()) {
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
