/**
 * Copyright 2017-2023 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Core
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
// External libs
import { DateFormatPipe, MomentModule } from 'ngx-moment';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FileSaverModule } from 'ngx-filesaver';
import { TranslateModule } from '@ngx-translate/core';
import { MonacoEditorModule } from 'ngx-monaco-editor-v2';
// Internal common
import { SharedModule } from '@shared/shared.module';
// Internal
import { scenarioRoute } from './scenario.routes';
import { ScenariosComponent } from './components/search-list/scenarios.component';
import {
    ScenarioExecutionsHistoryComponent
} from './components/execution/history/scenario-executions-history.component';
import { MoleculesModule } from '../../molecules/molecules.module';
import { RawEditionComponent } from './components/edition/raw/raw-edition.component';
import { DragulaModule } from 'ng2-dragula';
import {
    ScenarioCampaignsComponent
} from '@modules/scenarios/components/sub/scenario-campaigns/scenario-campaigns.component';
import { AuthoringInfoComponent } from './components/edition/authoring-info/authoring-info.component';
import { EditionInfoComponent } from './components/edition/edition-info/edition-info.component';
import { ScenarioExecutionService } from '@modules/scenarios/services/scenario-execution.service';
import { ScenarioExecutionComponent } from '@modules/scenarios/components/execution/detail/execution.component';
import {
    ScenarioExecutionsComponent
} from '@modules/scenarios/components/execution/history/list/scenario-executions.component';
import {
    ScenarioExecutionMenuComponent
} from '@modules/scenarios/components/execution/sub/right-side-bar/scenario-execution-menu.component';
import { ReportPreviewComponent } from './components/execution/preview/report-preview.component';
import { RxFor } from '@rx-angular/template/for';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { ScenarioJiraLinksModalComponent } from './components/scenario-jira-links-modal/scenario-jira-links-modal.component';

const ROUTES = [
    ...scenarioRoute
];


@NgModule({
    imports: [
        // Core
        CommonModule,
        RouterModule.forChild(ROUTES),
        ReactiveFormsModule,
        MomentModule,
        FormsModule,
        // External libs
        NgbModule,
        NgMultiSelectDropDownModule.forRoot(),
        TranslateModule,
        DragulaModule.forRoot(),
        FileSaverModule,
        MonacoEditorModule,
        // Internal common
        SharedModule,
        MoleculesModule,
        RxFor
    ],
    declarations: [
        ScenariosComponent,
        ScenarioExecutionComponent,
        ScenarioCampaignsComponent,
        ScenarioExecutionsHistoryComponent,
        RawEditionComponent,
        ScenarioExecutionMenuComponent,
        AuthoringInfoComponent,
        EditionInfoComponent,
        ScenarioExecutionsComponent,
        ReportPreviewComponent,
        ScenarioJiraLinksModalComponent
    ],
    providers: [
        DateFormatPipe,
        ScenarioExecutionService
    ]
})
export class ScenarioModule {
}
