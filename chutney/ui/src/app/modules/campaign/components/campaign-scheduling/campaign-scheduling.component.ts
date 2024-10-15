/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Component, Inject, OnInit } from '@angular/core';
import { Campaign, Environment } from '@core/model';
import { CampaignService, EnvironmentService } from '@core/services';
import { CampaignScheduling } from '@core/model/campaign/campaign-scheduling.model';
import { CampaignSchedulingService } from '@core/services/campaign-scheduling.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbDatepickerConfig, NgbDateStruct, NgbTimepickerConfig } from '@ng-bootstrap/ng-bootstrap';
import { NgbDate } from '@ng-bootstrap/ng-bootstrap/datepicker/ngb-date';
import { NgbTime } from '@ng-bootstrap/ng-bootstrap/timepicker/ngb-time';
import { FREQUENCY } from '@core/model/campaign/FREQUENCY';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { DROPDOWN_SETTINGS } from '@core/model/dropdown-settings';

@Component({
    selector: 'chutney-campaign-scheduling',
    templateUrl: './campaign-scheduling.component.html',
    styleUrls: ['./campaign-scheduling.component.scss']
})
export class CampaignSchedulingComponent implements OnInit {

    scheduledCampaigns: Array<CampaignScheduling> = [];
    form: FormGroup;
    errorMessage: string;
    submitted: boolean;
    frequencies = Object.values(FREQUENCY);
    campaigns: Array<Campaign> = [];
    environments: Array<Environment> = [];
    model: NgbDateStruct;

    constructor(private campaignSchedulingService: CampaignSchedulingService,
                private campaignService: CampaignService,
                private environmentService: EnvironmentService,
                private formBuilder: FormBuilder,
                private configTime: NgbTimepickerConfig,
                private configDate: NgbDatepickerConfig,
                @Inject(DROPDOWN_SETTINGS) public dropdownSettings: IDropdownSettings
    ) {
        dropdownSettings.textField = 'title'
        this.configTime.spinners = false;
        const currentDate = new Date();
        this.configDate.minDate = {
            year: currentDate.getFullYear(),
            month: currentDate.getMonth() + 1,
            day: currentDate.getDate()
        };
        this.configDate.maxDate = {year: currentDate.getFullYear() + 1, month: 12, day: 31};
    }

    ngOnInit() {
        this.environmentService.list().subscribe({
            next: (res) => this.environments = res,
            error: (error) => this.errorMessage = 'Cannot get environment list - ' + error
        });
        
        this.campaignService.findAllCampaigns().subscribe({
            next: (res) => this.campaigns = res,
            error: (error) => this.errorMessage = 'Cannot get campaign list - ' + error
        });

        this.loadSchedulingCampaign();


        this.form = this.formBuilder.group({
            selectedCampaigns: [[], Validators.required],
            date: ['', Validators.required],
            time: ['', Validators.required],
            frequency: [''],
            environment: ['']
        });
    }

    create() {
        this.submitted = true;
        const formValue = this.form.value;
        if (this.form.invalid) {
            return;
        }

        const date: NgbDate = formValue['date'];
        const time: NgbTime = formValue['time'];
        const campaignList: Array<Campaign> = this.form.get('selectedCampaigns').value;
        const dateTime = new Date(date.year, date.month - 1, date.day, time.hour, time.minute, 0, 0);
        dateTime.setHours(time.hour - dateTime.getTimezoneOffset() / 60);
        const frequency: FREQUENCY = formValue['frequency'];
        const environment: string = formValue['environment'];

        const schedulingCampaign = new CampaignScheduling(
            campaignList.map(campaign => campaign.id),
            campaignList.map(campaign => campaign.title),
            dateTime, 
            frequency,
            null,
            environment
        );

        this.campaignSchedulingService.create(schedulingCampaign).subscribe({
            next: () => {
                this.loadSchedulingCampaign();
                this.form.reset();
            },
            error: (error) => {
                this.errorMessage = 'Cannot create - ' + error;
            }
        });

        this.submitted = false;
    }

    delete(id: number) {
        this.campaignSchedulingService.delete(id).subscribe({
            next: () => {
                this.loadSchedulingCampaign();
            },
            error: (error) => {
                this.errorMessage = 'Cannot delete - ' + error;
            }
        });
    }

    unselectCampaign(campaign: Campaign) {
        const selectedCampaigns = this.form.get('selectedCampaigns').value.filter( (c: Campaign) => c !== campaign);
        this.form.get('selectedCampaigns').setValue([...selectedCampaigns]);
    }

    private loadSchedulingCampaign() {
        this.campaignSchedulingService.findAll().subscribe({
            next: (res) => {
                this.scheduledCampaigns = res;
            },
            error: (error) => {
                this.errorMessage = 'Cannot get scheduled campaigns - ' + error;
            }
        });
    }

    // convenience getter for easy access to form fields
    get f() {
        return this.form.controls;
    }
}
