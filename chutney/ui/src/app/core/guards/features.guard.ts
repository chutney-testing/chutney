/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, RouterStateSnapshot } from '@angular/router';
import { FeatureService } from '@core/feature/feature.service';
import { AlertService } from '@shared';
import { TranslateService } from '@ngx-translate/core';

export const featuresGuard : CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
    const canAccess = inject(FeatureService).active(route.data['feature']);
    if (!canAccess) {
        inject(AlertService).error(inject(TranslateService).instant('login.unauthorized'), { timeOut: 0, extendedTimeOut: 0, closeButton: true });
    }
    return canAccess;
}
