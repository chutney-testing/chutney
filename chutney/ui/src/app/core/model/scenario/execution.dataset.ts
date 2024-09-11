/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { KeyValue } from "@model";

export class ExecutionDataset {
    constructor(
    public constants?: Array<KeyValue>,
    public datatable?: Array<Array<KeyValue>>,
    public datasetId?: string) {}
}
