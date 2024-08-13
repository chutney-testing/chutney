/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { FREQUENCY } from '@core/model/campaign/FREQUENCY';


export class CampaignScheduling {

    constructor(
        public campaignsId: number[],
        public campaignsTitle: string[],
        public schedulingDate: Date,
        public frequency?: FREQUENCY,
        public id?: number
    ) {
    }
}
