/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { FREQUENCY } from '@core/model/campaign/FREQUENCY';

export interface CampaignExecutionRequest {
    campaignId: number;
    campaignTitle: string;
    datasetId: string;
}

export interface CampaignScheduling {
    id?: number;
    schedulingDate: Date; 
    frequency: FREQUENCY;
    environment: string;
    campaignExecutionRequest: CampaignExecutionRequest[];
}

