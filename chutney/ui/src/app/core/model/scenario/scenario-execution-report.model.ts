/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { StepExecutionReport } from '@core/model/scenario/step-execution-report.model';
import { ExecutionStatus } from './execution-status';
import { ExecutionDataset } from "@core/model/scenario/execution.dataset";

export class ScenarioExecutionReport {
    constructor(
        public executionId: string,
        public status: ExecutionStatus,
        public duration: number,
        public startDate: Date,
        public report: StepExecutionReport,
        public environment: string,
        public user: string,
        public scenarioName?: string,
        public error?: string,
        public contextVariables?: Map<string, Object>,
        public dataset?: ExecutionDataset
    ) { }
}
