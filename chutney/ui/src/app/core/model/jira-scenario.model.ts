/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

export class JiraScenario {
    constructor(
        public id: string,
        public chutneyId: string,
        public executionStatus?: string) {
    }
}

export class JiraDatasetLinks {
    constructor(
        public dataset: string,
        public jiraId: string) {
    }
}

export class JiraScenarioLinks {
    constructor(
        public id: string,
        public chutneyId: string,
        public datasetLinks: Object) {
    }
}

export class JiraTestExecutionScenarios {
    constructor(
        public id: string,
        public jiraScenarios: JiraScenario[]) {
    }
}

export enum XrayStatus {
    PASS = 'PASS',
    FAIL = 'FAIL'
}
