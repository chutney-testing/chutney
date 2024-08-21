/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import {ExecutionStatus} from "@core/model/scenario/execution-status";
import {CampaignExecutionReport, KeyValue} from "@core/model/index";

export class ExternalDataset {
    constructor(
        public constants: Array<KeyValue>,
        public datatable: Array<Array<KeyValue>>,
        public datasetId?: string
    ) {
    }
}
