/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Dataset } from "@core/model";

export class ScenarioExecutionReportOutline {
    constructor(
        public scenarioId?: string,
        public executionId?: number,
        public duration?: number,
        public scenarioName?: string,
        public status?: string,
        public startDate?: Date,
        public info: Array<string> = [],
        public error: Array<string> = [],
        public dataset?: Dataset
    ) {}
}
