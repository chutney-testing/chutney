/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { TranslateService } from "@ngx-translate/core";
import { Dataset, KeyValue } from "@model";
import { Injectable } from "@angular/core";

@Injectable({
    providedIn: 'root'
})
export class DatasetUtils {

    constructor(private translateService: TranslateService) {}

    public getDatasetName(dataset?: Dataset) {
        if (!dataset) return ''
        return this.getExecutionDatasetName({id: dataset.id, constants: dataset.uniqueValues, datatable: dataset.multipleValues})
    }

    public getExecutionDatasetName(dataset?: {id?: string, constants?: Array<KeyValue>, datatable?: Array<Array<KeyValue>> }) {
        if (dataset) {
            if (dataset?.id) {
                return dataset?.id
            } else if ((dataset.constants && (dataset.constants.length > 0)) || (dataset.datatable && (dataset.datatable.length > 0))) {
                return this.translateService.instant("dataset.customLabel")
            }
        }
        return ''
    }
}
