/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { CommonModule } from '@angular/common';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { DatasetSelectionComponent } from '@shared/components/dataset-selection/dataset-selection.component';
import { ThumbnailPipe } from '@shared/pipes/thumbnail.pipe';
import { MomentModule } from 'ngx-moment';
import { AlertService } from './alert.service';
import { ErrorInterceptor } from './error-interceptor.service';
import { EnvironmentComboComponent, ExecutionBadgeComponent } from './components';
import {
    HasAuthorizationDirective,
    ImplementationHostDirective,
    InputFocusDirective,
    ResizeDirective
} from './directives';
import { EventManagerService } from './event-manager.service';
import {
    ComponentSearchPipe,
    DataSetSearchPipe,
    DurationPipe,
    LinkifyPipe,
    ObjectAsEntryListPipe,
    PrettyPrintPipe,
    SafePipe,
    ScenarioCampaignSearchPipe,
    ScenarioSearchPipe,
    SearchTextPipe,
    SortByFieldPipe,
    StringifyPipe,
    TruncatePipe,
    WithoutScenarioPipe
} from '@shared/pipes';
import { StateService } from './state/state.service';
import { ChutneyEditorComponent } from '@shared/components/chutney-editor/chutney-editor.component';
import { HjsonParserService } from '@shared/hjson-parser/hjson-parser.service';

import { NgbModule, NgbDropdownModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { ChutneyMainHeaderComponent } from '@shared/components/layout/header/chutney-main-header.component';
import { ChutneyLeftMenuComponent } from '@shared/components/layout/left-menu/chutney-left-menu.component';
import { ChutneyRightMenuComponent } from '@shared/components/layout/right-menu/chutney-right-menu.component';
import { DistinctPipe } from './pipes/distinct.pipe';
import { ScenarioExecuteModalComponent } from './components/execute-modal/scenario-execute-modal.component';
import { DropdownModule } from 'primeng/dropdown';
import { FloatLabelModule } from 'primeng/floatlabel';
import {FormsDataGridComponent} from "@shared/components/dataset/forms-data-grid/forms-data-grid.component";
import {FormsKeyValueComponent} from "@shared/components/dataset/forms-key-value/forms-key-value.component";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        MomentModule,
        ReactiveFormsModule,
        RouterModule,
        TranslateModule,
        NgbModule,
        NgbTooltipModule,
        NgbDropdownModule,
        DropdownModule,
        FloatLabelModule,
    ],
    declarations: [
        ComponentSearchPipe,
        DataSetSearchPipe,
        DatasetSelectionComponent,
        DurationPipe,
        LinkifyPipe,
        EnvironmentComboComponent,
        ExecutionBadgeComponent,
        ImplementationHostDirective,
        InputFocusDirective,
        ResizeDirective,
        ObjectAsEntryListPipe,
        PrettyPrintPipe,
        SafePipe,
        ScenarioCampaignSearchPipe,
        ScenarioSearchPipe,
        SearchTextPipe,
        SortByFieldPipe,
        StringifyPipe,
        ThumbnailPipe,
        TruncatePipe,
        WithoutScenarioPipe,
        HasAuthorizationDirective,
        ChutneyEditorComponent,
        ChutneyMainHeaderComponent,
        ChutneyLeftMenuComponent,
        ChutneyRightMenuComponent,
        DistinctPipe,
        ScenarioExecuteModalComponent,
        FormsDataGridComponent,
        FormsKeyValueComponent
    ],
    exports: [
        ComponentSearchPipe,
        DataSetSearchPipe,
        DatasetSelectionComponent,
        DurationPipe,
        LinkifyPipe,
        EnvironmentComboComponent,
        ExecutionBadgeComponent,
        ImplementationHostDirective,
        InputFocusDirective,
        ResizeDirective,
        ObjectAsEntryListPipe,
        PrettyPrintPipe,
        SafePipe,
        ScenarioCampaignSearchPipe,
        ScenarioSearchPipe,
        SearchTextPipe,
        SortByFieldPipe,
        StringifyPipe,
        ThumbnailPipe,
        TruncatePipe,
        WithoutScenarioPipe,
        HasAuthorizationDirective,
        ChutneyEditorComponent,
        ChutneyRightMenuComponent,
        DistinctPipe,
        ScenarioExecuteModalComponent,
        FormsDataGridComponent,
        FormsKeyValueComponent
    ],
    providers: [
        {
            provide: HTTP_INTERCEPTORS,
            useClass: ErrorInterceptor,
            multi: true
        },
        AlertService,
        EventManagerService,
        StateService,
        HjsonParserService
    ]
})
export class SharedModule {
}
