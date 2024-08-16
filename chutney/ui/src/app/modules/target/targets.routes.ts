/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Routes } from '@angular/router';

import { Authorization } from '@model';
import { TargetsComponent } from '@modules/target/list/targets.component';
import { TargetComponent } from '@modules/target/details/target.component';
import { targetsResolver } from '@modules/target/resolver/targets-resolver.service';
import { environmentsNamesResolver } from '@core/services/environments-names.resolver';

export const targetsRoutes: Routes = [
    {
        path: '',
        component: TargetsComponent,
        data: { 'authorizations': [ Authorization.ENVIRONMENT_ACCESS ] }
    },
    {
        path: ':name',
        title: 'target details',
        component: TargetComponent,
        resolve: {targets: targetsResolver, environmentsNames: environmentsNamesResolver},
        data: { 'authorizations': [ Authorization.ADMIN_ACCESS ] }
    }
];
