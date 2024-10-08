/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Component, Input, OnChanges } from '@angular/core';

import { CampaignService } from '@core/services';
import { Authorization, Campaign } from '@model';

@Component({
    selector: 'chutney-scenario-campaigns',
    templateUrl: './scenario-campaigns.component.html',
    styleUrls: ['./scenario-campaigns.component.scss']
})
export class ScenarioCampaignsComponent implements OnChanges {

    @Input() idScenario: string;
    campaignsForScenario: Array<Campaign> = [];

    Authorization = Authorization;

    constructor(private campaignService: CampaignService) {
    }

    ngOnChanges() {
        if (this.idScenario) {
            this.load(this.idScenario);
        }
    }

    load(id) {
        this.campaignService.findAllCampaignsForScenario(id).subscribe(
            (response) => {
                this.campaignsForScenario = response;
            },
            (error) => {
                console.log(error);
            }
        );
    }
}
