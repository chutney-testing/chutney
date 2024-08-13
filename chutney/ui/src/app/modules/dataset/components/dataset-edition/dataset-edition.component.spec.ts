/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { TestBed, waitForAsync } from '@angular/core/testing';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';

import { MoleculesModule } from '../../../../molecules/molecules.module';

import { MomentModule } from 'ngx-moment';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { DatasetEditionComponent } from './dataset-edition.component';
import { DataSetService } from '@core/services';
import { of } from 'rxjs';
import { DatasetListComponent } from '@modules/dataset/components/dataset-list/dataset-list.component';
import {
    FormsKeyValueComponent
} from '@modules/dataset/components/dataset-edition/forms-key-value/forms-key-value.component';
import {
    FormsDataGridComponent
} from '@modules/dataset/components/dataset-edition/forms-data-grid/forms-data-grid.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FeatureService } from '@core/feature/feature.service';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { RouterModule } from '@angular/router';

describe('DatasetEditionComponent', () => {

    const dataSetService = jasmine.createSpyObj('DataSetService', ['findAll']);
    dataSetService.findAll.and.returnValue(of([]));

    const featureService = jasmine.createSpyObj('FeatureService', ['active']);
    featureService.active.and.returnValue(false);

    beforeEach(waitForAsync(() => {
        TestBed.resetTestingModule();
        TestBed.configureTestingModule({
            imports: [
                RouterModule.forRoot([]),
                TranslateModule.forRoot(),
                MoleculesModule,
                SharedModule,
                MomentModule,
                NgbModule,
                NgMultiSelectDropDownModule.forRoot(), // todo remove
                FormsModule,
                ReactiveFormsModule
            ],
            declarations: [
                DatasetListComponent,
                DatasetEditionComponent,
                FormsKeyValueComponent,
                FormsDataGridComponent
            ],
            providers: [
                {provide: DataSetService, useValue: dataSetService},
                {provide: FeatureService, useValue: featureService}
            ]
        }).compileComponents();
    }));

    it('should create the component DatasetEditionComponent', () => {
        const fixture = TestBed.createComponent(DatasetEditionComponent);
        fixture.detectChanges();

        const app = fixture.debugElement.componentInstance;
        expect(app).toBeTruthy();
    });

});


