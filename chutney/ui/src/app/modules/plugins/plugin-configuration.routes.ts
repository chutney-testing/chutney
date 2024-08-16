/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Routes } from '@angular/router';
import { PluginConfigurationComponent } from './components/plugin-configuration.component';
import { Authorization } from '@model';

export const PluginConfigurationRoute: Routes = [
    {
        path: '',
        component: PluginConfigurationComponent,
        data: { 'authorizations': [ Authorization.ADMIN_ACCESS ] }
    }
];
