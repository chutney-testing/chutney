/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { CampaignExecutionReport } from '.';

export class Campaign {

    constructor(public id?: number,
        public title: string = '',
        public description: string = '',
        public scenarios: Array<CampaignScenario> = [],
        public campaignExecutionReports: Array<CampaignExecutionReport> = [],
        public environment: string = '',
        public parallelRun?: false,
        public retryAuto?: false,
        public datasetId?: string,
        public tags: Array<string> = []) {
    }
}

export class CampaignScenario {
    constructor(public scenarioId: string, public datasetId: string = null) {}
}
