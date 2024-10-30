/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { TestBed, waitForAsync } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';

import { MoleculesModule } from '../../../../molecules/molecules.module';

import { MomentModule } from 'ngx-moment';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { DatasetListComponent } from './dataset-list.component';
import { DataSetService } from '@core/services';
import { of } from 'rxjs';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { DROPDOWN_SETTINGS, DropdownSettings } from '@core/model/dropdown-settings';
import { RouterModule } from '@angular/router';
import { OAuthService } from "angular-oauth2-oidc";
import { ToastrService } from 'ngx-toastr';
import { AlertService } from '@shared';

describe('DatasetListComponent', () => {

  const dataSetService = jasmine.createSpyObj('DataSetService', ['findAll']);
  const oAuthService = jasmine.createSpyObj('OAuthService', ['loadDiscoveryDocumentAndTryLogin', 'configure', 'initCodeFlow', 'logOut', 'getAccessToken']);
  const alertService = jasmine.createSpyObj('AlertService', ['error']);
  dataSetService.findAll.and.returnValue(of([]));
   beforeEach(waitForAsync(() => {
    TestBed.resetTestingModule();

    TestBed.configureTestingModule({
      imports: [
        RouterModule.forRoot([]),
        HttpClientTestingModule,
        TranslateModule.forRoot(),
        MoleculesModule,
        SharedModule,
        MomentModule,
        NgbModule,
          NgMultiSelectDropDownModule.forRoot(),
        FormsModule,
        ReactiveFormsModule,
      ],
      declarations: [
        DatasetListComponent
      ],
      providers: [
        { provide: DataSetService, useValue: dataSetService },
        { provide: AlertService, useValue: alertService },
        { provide: OAuthService, useValue: oAuthService },
        {provide: DROPDOWN_SETTINGS, useClass: DropdownSettings}
      ]
    }).compileComponents();
  }));

  it('should create the component DatasetListComponent', () => {
    const fixture = TestBed.createComponent(DatasetListComponent);
    fixture.detectChanges();

    const app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  });

});


