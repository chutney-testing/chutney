/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Routes } from '@angular/router';

import { Authorization } from '@model';
import { EnvironmentsComponent } from '@modules/environment/list/environments.component';
import { environmentsResolver } from '@core/services/environments.resolver';

export const environmentsRoutes: Routes = [
    {
        path: '',
        component: EnvironmentsComponent,
        resolve: {environments: environmentsResolver},
        data: { 'authorizations': [ Authorization.ENVIRONMENT_ACCESS ] }
    }
];
