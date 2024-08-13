/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Routes } from '@angular/router';
import { GlobalVariableEditionComponent } from './components/global-variable-edition/global-variable-edition.component';

import { authGuard } from '@core/guards';
import { Authorization } from '@model';

export const GlobalVariableRoute: Routes = [
    {
        path: '',
        component: GlobalVariableEditionComponent,
        canActivate: [authGuard],
        data: { 'authorizations': [ Authorization.GLOBAL_VAR_READ,Authorization.GLOBAL_VAR_WRITE ] }
    }
];
