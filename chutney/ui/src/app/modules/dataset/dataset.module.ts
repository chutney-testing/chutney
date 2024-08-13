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
import { MomentModule } from 'ngx-moment';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { TranslateModule } from '@ngx-translate/core';

import { SharedModule } from '@shared/shared.module';

import { DatasetListComponent } from './components/dataset-list/dataset-list.component';
import { DatasetRoute } from './dataset.routes';
import { MoleculesModule } from 'src/app/molecules/molecules.module';
import { DatasetEditionComponent } from './components/dataset-edition/dataset-edition.component';
import { AtomsModule } from '../../atoms/atoms.module';
import {
    FormsKeyValueComponent
} from '@modules/dataset/components/dataset-edition/forms-key-value/forms-key-value.component';
import {
    FormsDataGridComponent
} from '@modules/dataset/components/dataset-edition/forms-data-grid/forms-data-grid.component';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';


const ROUTES = [
    ...DatasetRoute
];

@NgModule({
    imports: [
        CommonModule,
        RouterModule.forChild(ROUTES),
        FormsModule,
        ReactiveFormsModule,
        SharedModule,
        NgbModule,
        MomentModule,
        TranslateModule,
        MoleculesModule,
        NgMultiSelectDropDownModule.forRoot(),
        AtomsModule
    ],
    declarations: [
        DatasetListComponent,
        DatasetEditionComponent,
        FormsKeyValueComponent,
        FormsDataGridComponent
    ]
})
export class DatasetModule {
}
