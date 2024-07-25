/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Component, inject, Input, OnInit } from "@angular/core";
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

    @Input() executeCallback: (env: string, dataset: string) => void;

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
            this.executeCallback(this.selectedEnv, this.selectedDataset?.id)
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
