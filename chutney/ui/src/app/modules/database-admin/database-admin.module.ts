/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';

import { DatabaseAdminRoute } from './database-admin.routes';

import { MoleculesModule } from '../../molecules/molecules.module';
import { DatabaseAdminComponent } from './components/database-admin.component';
import { DateFormatPipe, MomentModule } from 'ngx-moment';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {
    DatabaseAdminExecutionReportListComponent
} from './components/resultReportList/database-admin-report-list.component';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(DatabaseAdminRoute),
    FormsModule,
    TranslateModule,
    MoleculesModule,
    MomentModule,
    NgbModule,
    FormsModule,
    ReactiveFormsModule,
    NgMultiSelectDropDownModule.forRoot(),
  ],
  declarations: [DatabaseAdminComponent, DatabaseAdminExecutionReportListComponent],
  providers: [DateFormatPipe]
})
export class DatabaseAdminModule {
}
