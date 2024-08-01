/**
 * Copyright 2017-2024 Enedis
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

import { Component, inject, OnInit } from "@angular/core";
import { Dataset } from "@core/model";
import { DataSetService, EnvironmentService } from "@core/services";
import { NgbActiveModal } from "@ng-bootstrap/ng-bootstrap";
import { TranslateService } from "@ngx-translate/core";
import { EventManagerService } from "@shared";


@Component({
    selector: 'scenario-execute-modal',
    templateUrl: './scenario-execute-modal.component.html',
    styleUrls: ['./scenario-execute-modal.component.scss']
})
export class ScenarioExecuteModalComponent implements OnInit {

    activeModal = inject(NgbActiveModal);

    environments: string[];
    datasets: Array<Dataset>;
    filteredDatasets: Array<Dataset>;

    selectedEnv: string = null;
    selectedDataset: Dataset = null;
    datasetDetails: Dataset = null;

    isCollapsed = true;

    errorMessage = "";

    constructor(
        private datasetService: DataSetService,
        private environmentService: EnvironmentService,
        private eventManagerService: EventManagerService,
        private translateService: TranslateService) {
    }


    ngOnInit(): void {
        this.datasetService.findAll().subscribe((res: Array<Dataset>) => {
            this.datasets = res;
            this.filteredDatasets = res;
        });
        this.environmentService.names().subscribe((res: string[]) => {
            this.environments = res;
            if (this.environments.length === 1) {
                this.selectedEnv = this.environments[0];
            }
        });
    }

    execute() {
        if (this.selectedEnv) {
            this.eventManagerService.broadcast({ name: 'execute', env: this.selectedEnv, dataset: this.selectedDataset?.id });
            this.activeModal.close();
        } else {
            this.translateService.get('scenarios.execution.errors.environment').subscribe((res: string) => {
                this.errorMessage = res;
            });
        }
    }

    datasetFilter(event: any) {
        const query = event.target.value.toLowerCase();
        this.filteredDatasets = this.datasets.filter(dataset => dataset.name.toLowerCase().includes(query) || dataset.tags.join(";").toLowerCase().includes(query));
    }

    getDatasetDetails() {
        if (this.selectedDataset) {
            this.datasetService.findById(this.selectedDataset?.id).subscribe((res: Dataset) => {
                this.datasetDetails = res;
            });
        } else {
            this.datasetDetails = null;
            this.isCollapsed = true;
        }
    }

    showHideDataset(event: any) {
        event.stopPropagation();
        this.isCollapsed = !this.isCollapsed;
    }
}
