/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Component, inject, Input, OnInit } from "@angular/core";
import {Dataset, KeyValue} from "@core/model";
import { DataSetService, EnvironmentService } from "@core/services";
import { NgbActiveModal } from "@ng-bootstrap/ng-bootstrap";
import { TranslateService } from "@ngx-translate/core";
import { EventManagerService } from "@shared";
import {catchError, map, switchMap} from "rxjs/operators";
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {Observable, of} from "rxjs";


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
    createDataset: Dataset = null;

    datasetForm: FormGroup;
    activeTab = 'keyValue';
    editionDataset: boolean = false;
    editionDatasetValues?: Dataset;


    @Input() executeCallback: (env: string, dataset: string) => void;
    @Input() changeModalSize: (size: "lg" | "xl") => void;

    isCollapsed = true;

    errorMessage = "";

    constructor(
        private datasetService: DataSetService,
        private environmentService: EnvironmentService,
        private eventManagerService: EventManagerService,
        private formBuilder: FormBuilder,
        private translateService: TranslateService){
    }


    ngOnInit(): void {
        this.translateService.get('dataset.actions.createDataset').pipe(
            map((res: string) => this.createDataset = new Dataset(res, "", [], new Date(), [], [])),
            map(createDataset => {
                this.datasetService.findAll().subscribe((res: Array<Dataset>) => {
                    this.datasets = [...res, createDataset];
                    this.filteredDatasets = [...res, createDataset];
            });
        })).subscribe();

        this.datasetForm = this.formBuilder.group({
            saveDatasetName: new FormControl(),
            saveDatasetCheckbox: [false, Validators.required],
            keyValues: new FormControl(),
            multiKeyValues: new FormControl()
        });

        this.environmentService.names().subscribe((res: string[]) => {
            this.environments = res;
            if (this.environments.length === 1) {
                this.selectedEnv = this.environments[0];
            }
        });

        this.saveDatasetCheckboxChanged();
    }

    selectedDatasetChanged() {
        if (this.selectedDataset == this.createDataset) {
            this.editDataset(this.selectedDataset, true);
        } else {
            this.editionDataset = false;
            this.changeModalSize("lg")
        }
    }

    executeModal() {
        if (this.editionDataset) {
            const editedDataset = this.buildDataset();
            if (this.validateDatasetCreation(editedDataset)) {
                this.errorMessage = "TOTOTOTOTOT" // TODO
                return;
            }
            if (this.datasetForm.get("saveDatasetCheckbox").value) {
                this.saveDataset(editedDataset).subscribe(dataset => {
                    if (dataset) {
                        this.execute(dataset)
                    }
                })
            } else {
                console.log(editedDataset)
                this.execute(editedDataset)
            }
        } else {
            this.execute(this.selectedDataset)
        }
    }

    execute(dataset: Dataset) {
        if (this.selectedEnv) {
            this.executeCallback(this.selectedEnv, dataset?.id)
            this.activeModal.close();
        } else {
            this.translateService.get('scenarios.execution.errors.environment').subscribe((res: string) => {
                this.errorMessage = res;
            });
        }
    }

    saveDataset(datasetToSave): Observable<Dataset> {
        return this.datasetService.save(datasetToSave).pipe(
            catchError(error => {
                console.error('Error while saving dataset : ', error);
                return of(null);
            })
        )
    }

    validateDatasetCreation(dataset: Dataset) {
        return this.editionDatasetValues && this.datasetForm.get("saveDatasetName").valid &&
            (dataset.multipleValues.length == 0 && dataset.uniqueValues.length == 0)
    }

    buildDataset() {
        const kv = this.datasetForm.controls['keyValues'] as FormArray;
        const keyValues = kv.value ? kv.value.map((p) => new KeyValue(p.key, p.value)) : [];

        const mkv = this.datasetForm.controls['multiKeyValues'] as FormArray;
        const multiKeyValues = mkv.value ? mkv.value.map(a => a.map((p) => new KeyValue(p.key, p.value))) : [];

        return new Dataset(
            this.datasetForm.get("saveDatasetName").value,
            "Created from dataset with id " + this.selectedDataset.id + " in execution panel",
            [],
            new Date(),
            keyValues,
            multiKeyValues
        );

    }

    selectTab(tab: string) {
        this.activeTab = tab;
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

    editDataset(dataset: Dataset, forceEditionDataset? : boolean) {
        this.editionDataset = forceEditionDataset || !this.editionDataset
        if (this.editionDataset) {
            this.changeModalSize("xl")
            this.editionDatasetValues = dataset;
            this.datasetForm.controls['keyValues'].patchValue(this.editionDatasetValues.uniqueValues);
            this.datasetForm.controls['multiKeyValues'].patchValue(this.editionDatasetValues.multipleValues);
        } else {
            this.changeModalSize("lg")
        }
    }

    saveDatasetCheckboxChanged() {
        this.datasetForm.get('saveDatasetCheckbox')?.valueChanges.subscribe(enable => {
            const saveDatasetName = this.datasetForm.get('saveDatasetName');
            if (enable) {
                saveDatasetName?.enable()
                saveDatasetName.setValidators([Validators.required])
            } else {
                saveDatasetName?.disable();
                saveDatasetName.setValidators([])
            }
        });
    }

    get datasetFormControl() {
        return this.datasetForm.controls;
    }
}
