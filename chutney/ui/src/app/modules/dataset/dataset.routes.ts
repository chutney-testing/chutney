/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Routes } from '@angular/router';

import { DatasetListComponent } from './components/dataset-list/dataset-list.component';
import { DatasetEditionComponent } from './components/dataset-edition/dataset-edition.component';
import { authGuard, canDeactivateGuard } from '@core/guards';
import { Authorization } from '@model';

export const DatasetRoute: Routes = [
    {
        path: '',
        component: DatasetListComponent,
        canActivate: [authGuard],
        data: { 'authorizations': [ Authorization.DATASET_READ,Authorization.DATASET_WRITE ] }
    },
    {
        path: ':id/edition',
        component: DatasetEditionComponent,
        canDeactivate: [canDeactivateGuard],
        canActivate: [authGuard],
        data: { 'authorizations': [ Authorization.DATASET_WRITE ] }
    },
    {
        path: 'edition',
        component: DatasetEditionComponent,
        canDeactivate: [canDeactivateGuard],
        canActivate: [authGuard],
        data: { 'authorizations': [ Authorization.DATASET_WRITE ] }
    }
];
