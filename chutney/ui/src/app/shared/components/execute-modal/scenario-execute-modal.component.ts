/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { ChangeDetectorRef, Component, inject, Input, OnInit } from "@angular/core";
import { Dataset, KeyValue } from "@core/model";
import { DataSetService, EnvironmentService } from "@core/services";
import { NgbActiveModal } from "@ng-bootstrap/ng-bootstrap";
import { TranslateService } from "@ngx-translate/core";
import { catchError } from "rxjs/operators";
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import { firstValueFrom, Observable, of } from "rxjs";


@Component({
    selector: 'scenario-execute-modal',
    templateUrl: './scenario-execute-modal.component.html',
    styleUrls: ['./scenario-execute-modal.component.scss']
})
export class ScenarioExecuteModalComponent implements OnInit {

    activeModal = inject(NgbActiveModal);

    environments: string[];
    environmentsLoaded = false;
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


    @Input() executeCallback: (env: string, dataset: Dataset) => void;
    @Input() changeModalSize: (size: "lg" | "xl") => void;

    isCollapsed = true;

    errorMessage = "";

    constructor(
        private datasetService: DataSetService,
        private environmentService: EnvironmentService,
        private formBuilder: FormBuilder,
        private translateService: TranslateService,
        private changeDetectorRef: ChangeDetectorRef){
    }


    ngOnInit(): void {
        const createDatasetLabel = this.translateService.instant('dataset.actions.createDataset');
        this.createDataset = new Dataset(createDatasetLabel, "", [], new Date(), [], [])
        this.datasetService.findAll().subscribe((res: Array<Dataset>) => {
                this.datasets = [...res, this.createDataset];
                this.filteredDatasets = [...res, this.createDataset];
        });

        this.datasetForm = this.formBuilder.group({
            saveDatasetName: new FormControl({ value: '', disabled: true }),
            saveDatasetCheckbox: [false, Validators.required],
            keyValues: new FormControl(),
            multiKeyValues: new FormControl()
        });

        this.environmentService.names().subscribe((res: string[]) => {
            this.environments = res;
            if (this.environments.length === 1) {
                this.selectedEnv = this.environments[0];
            }
            this.environmentsLoaded = true;
        });

        this.saveDatasetCheckboxChanged();
    }

    selectedDatasetChanged() {
        if (this.selectedDataset == this.createDataset) {
            this.editDataset(null, this.selectedDataset, true);
        } else {
            this.editionDataset = false;
            this.changeModalSize("lg")
        }
    }

    async executeModal() {
        let dataset : Dataset = this.selectedDataset
        if (this.editionDataset) {
            const editedDataset = this.buildDataset();
            if (this.validateDatasetCreation(editedDataset)) {
                this.errorMessage = this.translateService.instant("scenarios.execution.modal.error.invalidDataset")
                return;
            }
            if (this.datasetForm.get("saveDatasetCheckbox").value) {
                const datasetName = editedDataset.name
                if (!datasetName || datasetName.trim() === '') {
                    this.errorMessage = this.translateService.instant("scenarios.execution.modal.error.datasetEmptyName")
                    return;
                }
                const savedDataset = await firstValueFrom(this.saveDataset(editedDataset))
                if (!savedDataset) {
                    return;
                }
                dataset = savedDataset
            } else {
                dataset = editedDataset
            }
        }
        this.execute(dataset)
    }

    execute(dataset: Dataset) {
        if (this.selectedEnv) {
            this.executeCallback(this.selectedEnv, dataset)
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
                this.errorMessage = error.message
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

        if (this.selectedDataset && this.selectedDataset.id
            && this.compareDatatable(multiKeyValues, this.selectedDataset.multipleValues)
            && this.compareKeyValueArraysUnordered(keyValues, this.selectedDataset.uniqueValues)) {
            return this.selectedDataset // The dataset is in edition mode but has not been edited, return the original dataset
        }

        return new Dataset(
            this.datasetForm.get("saveDatasetName").value,
            this.selectedDataset.id ? "Created from " + this.selectedDataset.name : "Inline",
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
        if (this.selectedDataset && this.selectedDataset?.id) {
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

    editDataset(event: any, dataset: Dataset, forceEditionDataset? : boolean) {
        if (event) {
            event.stopPropagation();
        }
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
                saveDatasetName.markAsUntouched()
                saveDatasetName.setValidators([Validators.required])
            } else {
                saveDatasetName?.disable();
                saveDatasetName.setValidators([])
            }
            this.changeDetectorRef.detectChanges()
        });
    }

    get datasetFormControl() {
        return this.datasetForm.controls;
    }

    private compareDatatable(datatable1: Array<Array<KeyValue>>, datatable2: Array<Array<KeyValue>>) {
        if (datatable1.length !== datatable2.length) {
            return false;
        }
        for (let i = 0; i < datatable1.length; i++) {
            const innerArr1 = datatable1[i];
            const innerArr2 = datatable2[i];
            if (!this.compareKeyValueArraysUnordered(innerArr1, innerArr2)) {
                return false;
            }
        }
        return true;
    }

    private compareKeyValueArraysUnordered(array1: Array<KeyValue>, array2: Array<KeyValue>): boolean {
        if (array1.length !== array2.length) {
            return false;
        }
        const sortedArray1 = array1.slice().sort((a, b) => a.key.localeCompare(b.key));
        const sortedArray2 = array2.slice().sort((a, b) => a.key.localeCompare(b.key));
        for (let i = 0; i < sortedArray1.length; i++) {
            if (!sortedArray1[i].equals(sortedArray2[i])) {
                return false;
            }
        }
        return true;
    }
}
