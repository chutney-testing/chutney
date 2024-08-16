/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EnvironmentsVariablesComponent } from '@modules/environment-variable/list/environments-variables.component';
import { EnvironmentVariableRoutes } from '@modules/environment-variable/environment-variable.routes';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MoleculesModule } from '../../molecules/molecules.module';
import { NgbNavModule, NgbTooltipModule, NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { TranslateModule } from '@ngx-translate/core';


@NgModule({
  declarations: [
    EnvironmentsVariablesComponent
  ],
    imports: [
        CommonModule,
        EnvironmentVariableRoutes,
        FormsModule,
        MoleculesModule,
        NgbNavModule,
        NgbTooltipModule,
        NgbTypeaheadModule,
        TranslateModule,
        ReactiveFormsModule
    ]
})
export class EnvironmentVariableModule { }
