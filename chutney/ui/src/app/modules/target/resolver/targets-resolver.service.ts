/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { ActivatedRouteSnapshot, ResolveFn } from '@angular/router';
import { of } from 'rxjs';
import { Target, TargetFilter } from '@model';
import { inject } from '@angular/core';
import { EnvironmentService } from '@core/services';

export const targetsResolver: ResolveFn<Target[]> =
    (route: ActivatedRouteSnapshot) => {
        const name = route.params['name'];
        return name === 'new' ? of([]) : inject(EnvironmentService).getTargets(new TargetFilter(name, null));

    };
