/**
 * Copyright 2017-2023 Enedis
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
