/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Routes } from '@angular/router';
import { BackupsAdminComponent } from './components/backups-admin.component';

export const BackupsAdminRoute: Routes = [
    {
        path: '',
        component: BackupsAdminComponent
    }
];
