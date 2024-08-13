/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { TranslateModule } from '@ngx-translate/core';
import { MomentModule } from 'ngx-moment';
import { NgxPaginationModule } from 'ngx-pagination';

import { AtomsModule } from '../atoms/atoms.module';
import { SharedModule } from '@shared/shared.module';

import { ConfirmDialogComponent } from './dialog/delete-confirm-dialog/confirm-dialog.component';
import { CollapsiblePanelComponent } from './panel/collapsible-panel/collapsible-panel.component';
import { ErrorPanelComponent } from './panel/error-panel/error-panel.component';
import { PropertyTablePanelComponent } from './panel/property-table-panel/property-table-panel.component';
import { TablePanelComponent } from './panel/table-panel/table-panel.component';
import { EditableLabelComponent } from './forms/editable-label/editable-label.component';
import { InputLineComponent } from './forms/input-line/input-line.component';
import { SearchFieldComponent } from './forms/search-field/search-field.component';
import { ValidationService } from './validation/validation.service';
import { EditableTextAreaComponent } from './forms/editable-text-area/editable-text-area.component';
import { EditableBadgeComponent } from './forms/editable-badge/editable-badge.component';
import { AsciiDoctorComponent } from './asciidoctor/asciidoctor.component';
import { ToastInfoComponent } from './toast/toast-info/toast-info';
import { ImportFileComponent } from './forms/import-file/import-file.component';
import { ImportButtonComponent } from './forms/import-button/import-button.component';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';


@NgModule({
    imports: [
        AtomsModule,
        CommonModule,
        FormsModule,
        MomentModule,
        NgxPaginationModule,
        RouterModule,
        SharedModule,
        TranslateModule,
        NgbTooltipModule,
        ReactiveFormsModule
    ],
    exports: [
        AsciiDoctorComponent,
        CollapsiblePanelComponent,
        ConfirmDialogComponent,
        EditableBadgeComponent,
        EditableLabelComponent,
        EditableTextAreaComponent,
        ErrorPanelComponent,
        ImportButtonComponent,
        ImportFileComponent,
        InputLineComponent,
        //MenuItemComponent,
        PropertyTablePanelComponent,
        SearchFieldComponent,
        TablePanelComponent,
        ToastInfoComponent,
    ],
    declarations: [
        AsciiDoctorComponent,
        CollapsiblePanelComponent,
        ConfirmDialogComponent,
        EditableBadgeComponent,
        EditableLabelComponent,
        EditableTextAreaComponent,
        ErrorPanelComponent,
        ImportButtonComponent,
        ImportFileComponent,
        InputLineComponent,
       //MenuItemComponent,
        PropertyTablePanelComponent,
        SearchFieldComponent,
        TablePanelComponent,
        ToastInfoComponent,
    ],
    providers: [
        ValidationService,
    ]
})
export class MoleculesModule {
}
