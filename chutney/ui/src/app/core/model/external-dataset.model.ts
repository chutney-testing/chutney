/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import {ExecutionStatus} from "@core/model/scenario/execution-status";
import {CampaignExecutionReport, KeyValue} from "@core/model/index";

export interface ExternalDataset {
    constants: Array<KeyValue>,
    datatable: Array<Array<KeyValue>>,
    datasetId?: string
}
