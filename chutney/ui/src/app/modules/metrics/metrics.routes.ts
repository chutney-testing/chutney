/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Routes } from '@angular/router';
import { MetricsComponent } from './components/metrics.component';

export const MetricsRoute: Routes = [
    {
        path: '',
        component: MetricsComponent
    }
];
