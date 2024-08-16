/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Routes } from '@angular/router';
import { ScenariosComponent } from './components/search-list/scenarios.component';
import { RawEditionComponent } from './components/edition/raw/raw-edition.component';
import { authGuard, canDeactivateGuard } from '@core/guards';
import { Authorization } from '@model';
import {
    ScenarioExecutionsHistoryComponent
} from '@modules/scenarios/components/execution/history/scenario-executions-history.component';
import {
    ScenarioExecutionMenuComponent
} from '@modules/scenarios/components/execution/sub/right-side-bar/scenario-execution-menu.component';
import { ReportPreviewComponent } from './components/execution/preview/report-preview.component';

export const scenarioRoute: Routes = [

    {
        path: '',
        component: ScenariosComponent,
        canActivate: [authGuard],
        data: {'authorizations': [Authorization.SCENARIO_READ]}
    },
    {
        path: ':id/executions',
        canActivate: [authGuard],
        data: {'authorizations': [Authorization.SCENARIO_READ]},
        children: [
            {
                path: '',
                component: ScenarioExecutionsHistoryComponent
            },
            {
                path: '',
                component: ScenarioExecutionMenuComponent,
                outlet: 'right-side-bar'
            },
        ]
    },
    {
        path: ':id/raw-edition',
        component: RawEditionComponent,
        canDeactivate: [canDeactivateGuard],
        canActivate: [authGuard],
        data: {'authorizations': [Authorization.SCENARIO_WRITE]}
    },
    {
        path: 'raw-edition',
        component: RawEditionComponent,
        canDeactivate: [canDeactivateGuard],
        canActivate: [authGuard],
        data: {'authorizations': [Authorization.SCENARIO_WRITE]}
    },
    {
        path: 'report-preview',
        component: ReportPreviewComponent,
        canActivate: [authGuard],
        data: {'authorizations': [Authorization.ADMIN_ACCESS]}
    }
];
