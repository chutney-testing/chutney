/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EnvironmentsVariablesComponent } from '@modules/environment-variable/list/environments-variables.component';

const routes: Routes = [
    {
        path: '',
        component: EnvironmentsVariablesComponent,
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class EnvironmentVariableRoutes {
}
