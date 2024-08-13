/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { DataSetService } from '@core/services';
import { Dataset } from '@model';


@Component({
    selector: 'chutney-dataset-selection',
    templateUrl: './dataset-selection.component.html',
    styleUrls: ['./dataset-selection.component.scss']
})
export class DatasetSelectionComponent implements OnInit {

    @Input() selectedDatasetId: String;
    @Output() selectionEvent = new EventEmitter();

    datasets: Array<Dataset>;

    constructor(private datasetService: DataSetService) {}

    ngOnInit(): void {
        this.datasetService.findAll().subscribe((res: Array<Dataset>) => {
            this.datasets = res;
        });
    }

    changingValue(event: any) {
        this.selectionEvent.emit(event.target.value);
    }

}
