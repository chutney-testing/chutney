/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Routes } from '@angular/router';
import { DatabaseAdminComponent } from './components/database-admin.component';

export const DatabaseAdminRoute: Routes = [
    {
        path: '',
        component: DatabaseAdminComponent
    }
];
