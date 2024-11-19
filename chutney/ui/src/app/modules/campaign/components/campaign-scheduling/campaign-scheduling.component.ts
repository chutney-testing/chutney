/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Component, Inject, OnInit } from '@angular/core';
import { Campaign, Dataset, Environment } from '@core/model';
import { CampaignService, DataSetService, EnvironmentService } from '@core/services';
import { CampaignExecutionRequest, CampaignScheduling } from '@core/model/campaign/campaign-scheduling.model';
import { CampaignSchedulingService } from '@core/services/campaign-scheduling.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbDatepickerConfig, NgbDateStruct, NgbTimepickerConfig } from '@ng-bootstrap/ng-bootstrap';
import { NgbDate } from '@ng-bootstrap/ng-bootstrap/datepicker/ngb-date';
import { NgbTime } from '@ng-bootstrap/ng-bootstrap/timepicker/ngb-time';
import { FREQUENCY } from '@core/model/campaign/FREQUENCY';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { DROPDOWN_SETTINGS, DropdownSettings } from '@core/model/dropdown-settings';
import { ListItem } from 'ng-multiselect-dropdown/multiselect.model';
import { TranslateService } from '@ngx-translate/core';

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

    datasets: ListItem[] = [];
    datasetsSelected: Array<{"campaign": Campaign, "dataset": ListItem}> = [];
    datasetDropdownSettings: IDropdownSettings;
    EMPTY_DATASET = {"id": "", "text": ""};

    constructor(private campaignSchedulingService: CampaignSchedulingService,
                private campaignService: CampaignService,
                private environmentService: EnvironmentService,
                private formBuilder: FormBuilder,
                private configTime: NgbTimepickerConfig,
                private configDate: NgbDatepickerConfig,
                @Inject(DROPDOWN_SETTINGS) public dropdownSettings: IDropdownSettings,
                private translate: TranslateService,
                private datasetService: DataSetService

    ) {
        dropdownSettings.textField = 'title'
        this.datasetDropdownSettings = new DropdownSettings(translate)
        this.datasetDropdownSettings.textField = 'text'

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
        this.datasetDropdownSettings = {...this.datasetDropdownSettings, singleSelection: true}

        this.environmentService.list().subscribe({
            next: (res) => this.environments = res,
            error: (error) => this.errorMessage = 'Cannot get environment list - ' + error
        });

        this.datasetService.findAll().subscribe((res: Array<Dataset>) => {
            this.datasets = res.map(dataset => {
                return {"id": dataset.id, "text": dataset.name}
            });
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

        const campaignExecutionRequests: CampaignExecutionRequest[] = campaignList.map((campaign, index) => {
            const dataset = this.datasetsSelected[index];  // Assurez-vous que les listes ont la même longueur et que l'index est valide
        
            return {
                campaignId: campaign.id,
                campaignTitle: campaign.title,
                datasetId: dataset.dataset?.id.toString() || "" // On utilise une valeur par défaut (""), au cas où `dataset.dataset?.id` soit `undefined`
            };
        });
        
        const schedulingCampaign: CampaignScheduling = {
            schedulingDate: dateTime, 
            frequency: frequency,
            environment: environment,
            campaignExecutionRequest: campaignExecutionRequests
        };

        this.campaignSchedulingService.create(schedulingCampaign).subscribe({
            next: () => {
                this.loadSchedulingCampaign();
                this.form.reset();
            },
            error: (error) => {
                this.errorMessage = 'Cannot create - ' + error;
            }
        });

        this.datasetsSelected = [];
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

    selectCampaign(campaign: Campaign) {
        this.datasetsSelected.push({campaign: campaign, dataset: this.EMPTY_DATASET});
    }

    unselectCampaign(campaign: Campaign) {
        const selectedCampaigns = this.form.get('selectedCampaigns').value.filter( (c: Campaign) => c.id !== campaign.id);
        this.form.get('selectedCampaigns').setValue([...selectedCampaigns]);

        const index = this.datasetsSelected.findIndex(elt => elt.campaign.id === campaign.id)
        this.datasetsSelected.splice(index, 1);
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

    selectDataset(toAdd: ListItem, campaign: Campaign) {
        const foundelt = this.datasetsSelected.find(elt => elt.campaign.id === campaign.id)
        foundelt.dataset = toAdd;
    }

    unselectDataset(toRemove: Campaign) {
        const foundelt = this.datasetsSelected.find(elt => elt.campaign.id === toRemove.id)
        foundelt.dataset = this.EMPTY_DATASET;
    }

    // convenience getter for easy access to form fields
    get f() {
        return this.form.controls;
    }
}
