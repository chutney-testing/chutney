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

    selectDatasetId: string = null;
    selectedEnv: string = null;

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
            this.eventManagerService.broadcast({ name: 'execute', env: this.selectedEnv, dataset: this.selectDatasetId });
            this.activeModal.close();
        } else {
            this.translateService.get('scenarios.execution.errors.environment').subscribe((res: string) => {
                this.errorMessage = res;
            });
        }
    }

    selectDataset(event: any) {
        this.selectDatasetId = event.target.value;
    }

    selectEnvironment(event: any) {
        this.selectedEnv = event.target.value;
    }

}
