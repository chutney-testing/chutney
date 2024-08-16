/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Execution } from '@core/model/scenario/execution.model';

export class ScenarioIndex {

    public status;
    public lastExecution;

    constructor(
        public id?: string,
        public title?: string,
        public description?: string,
        public repositorySource?: string,
        public creationDate?: Date,
        public updateDate?: Date,
        public version?: number,
        public author?: string,
        public tags: Array<string> = [],
        public executions?: Array<Execution>,
        public jiraId?: string
    ) {
        this.status = this.findStatus();
        this.lastExecution = this.lastTimeExec();
    }

    private findStatus() {
        if (this.executions && this.executions.length > 0) {
            return this.executions[0].status;
        } else {
            return 'NOT_EXECUTED';
        }
    }

    private lastTimeExec() {
        if (this.executions && this.executions.length > 0) {
            return this.executions[0].time;
        } else {
            return null;
        }
    }
}
