/**
 * Copyright 2017-2023 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Observable, Subscription } from 'rxjs';
import { DragulaService } from 'ng2-dragula';

import { Campaign, CampaignScenario, Dataset, JiraScenario, ScenarioIndex } from '@model';
import {
    CampaignService,
    DataSetService,
    EnvironmentService,
    JiraPluginConfigurationService,
    JiraPluginService,
    ScenarioService
} from '@core/services';
import { distinct, flatMap, newInstance } from '@shared/tools/array-utils';
import { isNotEmpty } from '@shared';
import { DROPDOWN_SETTINGS } from '@core/model/dropdown-settings';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { ListItem } from 'ng-multiselect-dropdown/multiselect.model';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'chutney-campaign-edition',
    templateUrl: './campaign-edition.component.html',
    styleUrls: ['./campaign-edition.component.scss']
})
export class CampaignEditionComponent implements OnInit, OnDestroy {

    campaignForm: FormGroup;

    campaign = new Campaign();
    submitted: boolean;
    scenarios: Array<ScenarioIndex> = [];
    scenariosToAdd: Array<{"scenarioId": ScenarioIndex, "dataset": ListItem}> = [];
    errorMessage: any;
    subscription = new Subscription();
    datasets: ListItem[] = [];
    dropdownDatasetSettings: IDropdownSettings
    error: boolean = false;


    private routeParamsSubscription: Subscription;

    DRAGGABLE = 'DRAGGABLE';

    environments: Array<string>;
    selectedEnvironment: string;

    itemList: ListItem[] = [];
    jiraItemList: ListItem[] = [];
    selectedTags: string[] = [];
    jiraSelectedTags: string[] = [];
    datasetId: string;
    jiraId: string;
    jiraMap: Map<string, string> = new Map();
    jiraUrl = '';
    jiraScenarios: JiraScenario[] = [];
    jiraScenariosToExclude: Array<ScenarioIndex> = [];

    constructor(
        private campaignService: CampaignService,
        private scenarioService: ScenarioService,
        private jiraLinkService: JiraPluginService,
        private jiraPluginConfigurationService: JiraPluginConfigurationService,
        private formBuilder: FormBuilder,
        private router: Router,
        private route: ActivatedRoute,
        private dragulaService: DragulaService,
        private environmentService: EnvironmentService,
        private datasetService: DataSetService,
        private translate: TranslateService,
        @Inject(DROPDOWN_SETTINGS) public dropdownSettings: IDropdownSettings
    ) {
        this.campaignForm = this.formBuilder.group({
            title: ['', Validators.required],
            description: '',
            tags: [],
            jiratags: [],
            campaignTags: '',
            scenariosFilter: '',
            parallelRun: false,
            retryAuto: false,
            jiraId: '',
            onlyLinkedScenarios: false
        });
    }

    ngOnInit() {
        this.submitted = false;
        this.loadEnvironment();
        this.loadAllScenarios();
        this.datasetService.findAll().subscribe((res: Array<Dataset>) => {
            this.datasets = res.map(dataset => {
                return {"id": dataset.name, "text": dataset.name}
            });
        });
        this.dropdownDatasetSettings = {...this.dropdownSettings, singleSelection: true}
    }

    onItemSelect(item: any) {
        this.selectedTags.push(item.text);
        this.selectedTags = newInstance(this.selectedTags);
    }

    OnItemDeSelect(item: any) {
        this.selectedTags.splice(this.selectedTags.indexOf(item.text), 1);
        this.selectedTags = newInstance(this.selectedTags);
    }

    OnItemDeSelectAll() {
        this.selectedTags = newInstance([]);
    }

    onJiraItemSelect(item: any) {
        this.jiraSelectedTags.push(item.text);
        this.jiraSelectedTags = newInstance(this.jiraSelectedTags);
        this.jiraFilter();
    }

    OnJiraItemDeSelect(item: any) {
        this.jiraSelectedTags.splice(this.jiraSelectedTags.indexOf(item.text), 1);
        this.jiraSelectedTags = newInstance(this.jiraSelectedTags);
        this.jiraFilter();
    }

    OnJiraItemDeSelectAll() {
        this.jiraSelectedTags = newInstance([]);
        this.jiraFilter();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.dragulaService.destroy(this.DRAGGABLE);
    }

    load(id: number) {
        if (id != null) {
            this.campaignService.find(id).subscribe({
                next: (campaignFound) => {
                    this.campaign = campaignFound;
                    this.campaignForm.controls['title'].setValue(this.campaign.title);
                    this.campaignForm.controls['description'].setValue(this.campaign.description);
                    this.campaignForm.controls['parallelRun'].setValue(this.campaign.parallelRun);
                    this.campaignForm.controls['retryAuto'].setValue(this.campaign.retryAuto);
                    this.campaignForm.controls['campaignTags'].setValue(this.campaign.tags);
                    this.selectedEnvironment = this.campaign.environment;
                    this.setCampaignScenarios();
                    this.datasetId = this.campaign.datasetId;
                    this.initJiraPlugin();
                },
                error: (error) => {
                    this.errorMessage = error._body;
                }
            });
        }
    }

    loadAllScenarios() {
        this.subscription = this.scenarioService.findScenarios().subscribe({
            next: (res) => {
                this.scenarios = res;
                this.routeParamsSubscription = this.route.params.subscribe((params) => {
                    this.load(params['id']);
                });
                this.initTags();
            },
            error: (error) => {
                this.errorMessage = error.error;
            }
        });
    }

    private initTags() {
        const allTagsInScenario: string[] = distinct(flatMap(this.scenarios, (sc) => sc.tags)).sort();

        allTagsInScenario.forEach((currentValue, index) => {
            this.itemList.push({id: index, text: currentValue});
        });
    }

    loadEnvironment() {
        this.environmentService.names().subscribe({
            next: (res) => {
                this.environments = res.sort((t1, t2) => t1.toUpperCase() > t2.toUpperCase() ? 1 : 0);
            },
            error: (error) => {
                this.errorMessage = error.error;
            }
        });
    }

    loadJiraLink() {
        this.jiraLinkService.findByCampaignId(this.campaign.id).subscribe({
            next: (jiraId) => {
                this.campaignForm.controls['jiraId'].setValue(jiraId);
                this.refreshJiraScenarios();
            },
            error: (error) => {
                this.errorMessage = error.error;
            }
        });
    }

    initJiraPlugin() {
        this.jiraPluginConfigurationService.getUrl()
            .subscribe((r) => {
                if (r !== '') {
                    this.jiraUrl = r;
                    this.loadJiraLink();
                    this.jiraLinkService.findScenarios()
                        .subscribe(
                            (result) => {
                                this.jiraMap = result;
                            }
                        );
                }
            });
    }

    getJiraLink(id: string) {
        return this.jiraUrl + '/browse/' + this.jiraMap.get(id);
    }

    getJiraLastExecutionStatus(id: string) {
        const jiraScenario = this.jiraScenarios.filter(s => s.chutneyId === id);
        if (jiraScenario.length > 0) {
            return jiraScenario[0].executionStatus;
        } else {
            return '';
        }
    }

    getJiraLastExecutionStatusClass(id: string) {
        const status = this.getJiraLastExecutionStatus(id);
        switch (status) {
            case 'PASS' : return 'bg-success';
            case 'FAIL' : return 'bg-danger';
            default : return 'bg-secondary';
        }
    }

    hasJiraId() {
        return this.campaignForm.value['jiraId'] != null && this.campaignForm.value['jiraId'] !== '';
    }

    refreshJiraScenarios() {
        if (this.campaignForm.value['jiraId'] !== '') {
            this.jiraLinkService.findTestExecScenarios(this.campaignForm.value['jiraId'])
                .subscribe({
                    next: (result) => {
                        this.jiraScenarios = result;
                        let index = 0;
                        this.jiraScenarios.forEach((currentValue) => {
                            if (isNotEmpty(currentValue.executionStatus)) {
                                this.jiraItemList.push({'id': index, 'text': currentValue.executionStatus});
                                index++;
                            }
                        });
                        this.jiraFilter();
                    },
                    error: (error) => {
                        this.errorMessage = error.error;
                        this.clearJiraScenarios();
                    }
                });
        } else {
            this.clearJiraScenarios();
        }
    }

    clearJiraScenarios() {
        this.jiraScenarios = [];
        this.jiraScenariosToExclude = [];
        this.campaignForm.controls['onlyLinkedScenarios'].setValue(false);
    }

    jiraFilter() {
        if (this.campaignForm.controls['onlyLinkedScenarios'].value === true) {
            this.jiraScenariosToExclude = this.scenarios.filter((item) => {
                let jiraTagFilter = false;
                if (this.jiraSelectedTags.length > 0) {

                    jiraTagFilter = (this.jiraScenarios.find(s => item.id === s.chutneyId &&
                        this.jiraSelectedTags.includes(s.executionStatus))) === undefined;
                }
                return (!this.jiraScenarios.map(j => j.chutneyId).includes(item.id)) || jiraTagFilter;
            });
        } else {
            this.jiraScenariosToExclude = [];
        }
    }

    clear() {
        this.campaignForm.reset();
        let url: string;
        if (this.campaign.id) {
            url = '/campaign/' + this.campaign.id + '/executions';
        } else {
            url = '/campaign';
        }
        this.router.navigateByUrl(url);
    }

    saveCampaign() {
        this.submitted = true;
        const formValue = this.campaignForm.value;

        if (this.campaignForm.invalid) {
            return;
        }

        this.campaign.title = formValue['title'];
        this.campaign.description = formValue['description'];
        this.campaign.environment = this.selectedEnvironment;
        this.campaign.parallelRun = formValue['parallelRun'];
        this.campaign.retryAuto = formValue['retryAuto'];
        this.campaign.datasetId = this.datasetId;
        const tags = formValue['campaignTags'] + '';
        this.campaign.tags = tags.length !== 0 ? tags.split(',') : [];

        this.setCampaignScenariosIdsToAdd(this.scenariosToAdd);
        if (!this.error) {
            if (this.campaign.id != null) {
                this.subscribeToSaveResponse(
                    this.campaignService.update(this.campaign));
            } else {
                this.subscribeToSaveResponse(
                    this.campaignService.create(this.campaign));
            }
        }
    }

    setCampaignScenarios() {
        this.scenariosToAdd = [];
        if (this.campaign.scenarios) {
            for (const campaignScenario of this.campaign.scenarios) {
                const scenarioFound = this.scenarios.find((x) => x.id === campaignScenario.scenarioId);
                this.scenariosToAdd.push({scenarioId: scenarioFound, dataset: (campaignScenario.datasetId ? {id: campaignScenario.datasetId, text: campaignScenario.datasetId} : null)});
            }
        }
    }

    setCampaignScenariosIdsToAdd(scenariosToAdd: Array<{scenarioId: ScenarioIndex, dataset: ListItem}>) {
        this.error = false;
        this.campaign.scenarios = [];
        for (const scenario of scenariosToAdd) {
            if (!this.campaign.scenarios.some((s) => s.scenarioId === scenario.scenarioId.id && ((scenario.dataset === null && s.datasetId == null) || (scenario.dataset !== null && s.datasetId === scenario.dataset.id)))) {
                this.campaign.scenarios.push(new CampaignScenario(scenario.scenarioId.id, scenario.dataset ? scenario.dataset.id as string : null));
            } else {
                this.error = true;
                this.translate.get('campaigns.edition.errors.scenarioDatasetDuplicate', {
                    scenarioId: scenario.scenarioId.id,
                    datasetId: scenario.dataset ? scenario.dataset.id : "NULL"
                }).subscribe((msg: string) => {
                    this.errorMessage = msg
                });
                break;
            }
        }
        if (this.error) {
            // Clear campaign.scenarios
            this.campaign.scenarios.length = 0
        }
    }

    addScenario(scenario: ScenarioIndex) {
        this.scenariosToAdd.push({scenarioId: scenario, dataset: null});
        this.refreshForPipe();
    }

    removeScenario(scenario: {"scenarioId": ScenarioIndex, "dataset": ListItem}) {
        const index = this.scenariosToAdd.findIndex(scenarioElement => scenarioElement === scenario)
        this.scenariosToAdd.splice(index, 1);
        this.refreshForPipe();
    }

    private subscribeToSaveResponse(result: Observable<Campaign>) {
        result.subscribe({
            next: (res: Campaign) => this.onSaveSuccess(res),
            error: (error) => this.onSaveError(error)
        });
    }

    private onSaveSuccess(result: Campaign) {
        this.submitted = false;
        const url = '/campaign/' + result.id + '/executions';
        this.updateJiraLink(result.id);
        this.router.navigateByUrl(url);
    }

    private onSaveError(error) {
        console.log(error);
        try {
            error.json();
        } catch (exception) {
            error.message = error.text();
        }
        this.submitted = false;
        this.errorMessage = error.message;
    }

    private refreshForPipe() {
        // force instance to change for pipe refresh
        this.scenariosToAdd = Object.assign([], this.scenariosToAdd);
    }

    setSelectedEnvironment(event: string) {
        this.selectedEnvironment = event;
    }

    selectDataset(datasetId: string) {
        this.datasetId = datasetId;
    }

    selectDatasetScenario(dataset: ListItem, scenario: {"scenarioId": ScenarioIndex, "dataset": ListItem}) {
        const scenarioSelected = this.scenariosToAdd.find(scenarioElement => scenarioElement === scenario)
        scenarioSelected.dataset = dataset;
        this.refreshForPipe();
    }

    deselectDatasetScenario(scenario: {"scenarioId": ScenarioIndex, "dataset": ListItem}) {
        const scenarioSelected = this.scenariosToAdd.find(scenarioElement => scenarioElement === scenario)
        scenarioSelected.dataset = null;
        this.refreshForPipe();
    }

    private updateJiraLink(campaignId: number) {
        this.jiraId = this.campaignForm.value['jiraId'];
        this.jiraLinkService.saveForCampaign(campaignId, this.jiraId).subscribe({
            error: (error) => {
                this.errorMessage = error.error;
            }
        });
    }
}
