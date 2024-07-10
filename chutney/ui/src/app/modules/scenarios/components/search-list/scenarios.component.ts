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
import { interval, Observable, Subject, Subscription } from 'rxjs';
import { catchError, debounceTime, mergeMap, takeWhile, tap } from 'rxjs/operators';

import {
    distinct,
    filterOnTextContent,
    flatMap,
    intersection,
    newInstance,
    sortByAndOrder
} from '@shared/tools/array-utils';
import { StateService } from '@shared/state/state.service';
import { JiraPluginConfigurationService, JiraPluginService, ScenarioService } from '@core/services';
import { Authorization, ScenarioIndex } from '@model';
import { ExecutionStatus } from '@core/model/scenario/execution-status';
import { TranslateService } from '@ngx-translate/core';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { DROPDOWN_SETTINGS } from '@core/model/dropdown-settings';
import { ListItem } from 'ng-multiselect-dropdown/multiselect.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ScenarioJiraLinksModalComponent } from '../scenario-jira-links-modal/scenario-jira-links-modal.component';

@Component({
    selector: 'chutney-scenarios',
    templateUrl: './scenarios.component.html',
    styleUrls: ['./scenarios.component.scss']
})
export class ScenariosComponent implements OnInit, OnDestroy {

    urlParams: Subscription;
    reloadSubscription: Subscription;

    scenarios: Array<ScenarioIndex> = [];

    // Filter
    viewedScenarios: Array<ScenarioIndex> = [];
    textFilter: string;
    fullTextFilter: string;
    tags = [];
    selectedTags = [];
    fullTextSearch = false;
    status: ListItem[] = [];
    selectedStatus= [];
    // Jira
    jiraMap: Map<string, string> = new Map();
    jiraUrl = '';
    // Order
    orderBy = 'lastExecution';
    reverseOrder = false;

    private searchSub$ = new Subject<string>();

    Authorization = Authorization;


    constructor(
        private router: Router,
        private scenarioService: ScenarioService,
        private jiraLinkService: JiraPluginService,
        private jiraPluginConfigurationService: JiraPluginConfigurationService,
        private stateService: StateService,
        private readonly route: ActivatedRoute,
        private translateService: TranslateService,
        private modalService: NgbModal,
        @Inject(DROPDOWN_SETTINGS) public dropdownSettings: IDropdownSettings
    ) {
    }

    ngOnInit() {
        this.initJiraPlugin();
        this.onKeywordSearch();
        this.fetchAndUpdateScenario()
            .subscribe(res => {
                if (this.atLeastOneScenarioIsRunning(res)) {
                    this.subscribeForScenarios()
                }
            })

    }

    ngOnDestroy(): void {
        this.urlParams?.unsubscribe();
        this.reloadSubscription?.unsubscribe();
    }

    createNewScenario() {
        this.router.navigateByUrl('/scenario/raw-edition');
    }

    // Ordering //
    sortBy(property) {
        if (this.orderBy === property) {
            this.reverseOrder = !this.reverseOrder;
        }

        this.orderBy = property;
        this.applyFilters();
    }

    sortScenarios(property, reverseOrder) {
        this.viewedScenarios = sortByAndOrder(
            this.viewedScenarios,
            this.getKeyExtractorBy(property),
            reverseOrder
        );
    }

    // Filtering //
    updateTextFilter(text: string) {
        this.textFilter = text;
        this.applyFilters();
    }

    updateFullTextFilter(text: string) {
        this.fullTextFilter = text;
        this.applyFilters();
    }

    applyFilters() {
        if (this.fullTextFilter) {
            this.searchSub$.next(this.fullTextFilter);
        } else {
            this.localFilter(this.scenarios);
        }
    }

    onItemSelect() {
        this.stateService.changeTags(this.getSelectedTags());
        this.applyFilters();
    }

    OnItemDeSelect() {
        this.stateService.changeTags(this.getSelectedTags());
        this.applyFilters();
    }

    OnItemDeSelectAll() {
        this.selectedTags = newInstance([]);
        this.stateService.changeTags(this.getSelectedTags());
        this.applyFilters();
    }

    // Jira link //
    initJiraPlugin() {
        this.jiraPluginConfigurationService.getUrl()
            .subscribe((url) => {
                if (url !== '') {
                    this.jiraUrl = url;
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

    showScenarioJiraLinks(scenario: ScenarioIndex) {
        const modalRef = this.modalService.open(ScenarioJiraLinksModalComponent, { size: 'lg' });
		modalRef.componentInstance.scenario = scenario;
		modalRef.componentInstance.jiraUrl = this.jiraUrl;
    }

    private onKeywordSearch() {
        this.searchSub$.pipe(
            debounceTime(400),
            mergeMap(keyword => this.scenarioService.search(keyword))
        ).subscribe(scenarios => this.localFilter(scenarios));
    }

    private subscribeForScenarios() {
        this.reloadSubscription = interval(3000)
            .pipe(
                mergeMap(() => this.fetchAndUpdateScenario()),
                takeWhile(t => this.atLeastOneScenarioIsRunning(t)))
            .subscribe()
    }

    private fetchAndUpdateScenario(): Observable<Array<ScenarioIndex>> {
        return this.getScenarios()
            .pipe(
                tap(scenarios => {
                    if (!this.scenarios.length) {
                        this.scenarios = scenarios;
                    } else {
                        this.updateRunningScenarioStatus(scenarios);
                    }
                    this.applyDefaultState();
                    this.applySavedState();
                    this.applyUriState();
                }),
                catchError(err => [])
            );
    }

    private updateRunningScenarioStatus(scenarios: Array<ScenarioIndex>) {
        this.scenarios.filter(scenario => scenario.status === ExecutionStatus.RUNNING)
            .forEach(scenario => {
                scenario.status = scenarios.find(updatedScenario => updatedScenario.id === scenario.id).status;
            });
    }

    private atLeastOneScenarioIsRunning(scenarios: Array<ScenarioIndex>): boolean {
        return scenarios.filter(scenario => scenario.status === ExecutionStatus.RUNNING).length > 0
    }

    private initFilters() {
        const allTagsInScenario: string[] = this.findAllTags();
        this.tags = allTagsInScenario.map(tag => this.toSelectOption(tag,tag));
        this.status = [...new Set(this.scenarios.map(scenario => scenario.status))].map(status => this.toSelectOption(status, this.translateService.instant(ExecutionStatus.toString(status))));
    }

    private toSelectOption(id: string, label: string = id) {
        return {id: id, text: label };
    }

    private getScenarios(): Observable<Array<ScenarioIndex>> {
        return this.fullTextFilter ? this.scenarioService.search(this.fullTextFilter) : this.scenarioService.findScenarios();
    }

    private applyDefaultState() {
        this.initFilters();
    }

    private findAllTags() {
        return distinct(flatMap(this.scenarios, (sc) => sc.tags)).sort();
    }

    private applySavedState() {
        this.setSelectedTags();
    }

    private setSelectedTags() {
        const savedTags = this.stateService.getTags();
        if (savedTags != null) {
            this.selectedTags = this.tags.filter(tag => savedTags.includes(tag.id));
        }
    }

    private applyUriState() {
        this.urlParams = this.route.queryParams
            .pipe(
                tap({
                    next: (params: Array<any>) => {
                        this.textFilter = params['text'] || '';
                        if (params['orderBy']) {
                            this.orderBy = params['orderBy'];
                        }
                        if (params['status']) {
                            this.selectedStatus = this.status.filter((status) => params['status'].split(',').includes(status.text));
                        }
                        if (params['reverseOrder']) {
                            this.reverseOrder = params['reverseOrder'] === 'true';
                        }
                        if (params['tags']) {
                            const uriTag = params['tags'].split(',');
                            this.selectedTags = this.tags.filter(tag => uriTag.includes(tag.id));
                        }
                        this.applyFilters();
                        this.urlParams?.unsubscribe()
                    },
                    error: (error) => console.log(error)
                })
            )
            .subscribe();

    }

    private getKeyExtractorBy(property: string) {
        if (property === 'title') {
            return i => i[property] == null ? '' : i[property].toLowerCase();
        }
        if (property === 'lastExecution' || property === 'creationDate' || property === 'updateDate') {
            const now = Date.now();
            return i => i[property] == null ? now - 1491841324 /*2017-04-10T16:22:04*/ : now - Date.parse(i[property]);
        } else {
            return i => i[property] == null ? '' : i[property];
        }
    }

    private localFilter(scenarios: Array<ScenarioIndex>) {
        const scenariosWithJiraId = scenarios.map(sce => {
            const jiraId = this.jiraMap.get(sce.id);
            if (jiraId) {
                sce.jiraId = jiraId;
            }
            return sce;
        });
        this.viewedScenarios = filterOnTextContent(scenariosWithJiraId, this.textFilter, ['title', 'id', 'jiraId', 'tags']);
        this.viewedScenarios = this.filterOnAttributes();
        this.sortScenarios(this.orderBy, this.reverseOrder);
        this.applyFiltersToRoute();
    }

    private filterOnAttributes() {
        const input = this.viewedScenarios;

        const tags = this.getSelectedTags();

        return input.filter((scenario: ScenarioIndex) => {
            return this.tagPresent(tags, scenario) && this.scenarioStatusPresent(this.selectedStatus, scenario);
        });
    }

    private scenarioStatusPresent(statusFilter: any[], scenario: ScenarioIndex): boolean {
        if (statusFilter.length > 0) {
            return intersection(statusFilter.map((status) => status.id), [scenario.status]).length > 0;
        } else {
            return true;
        }
    }

    private applyFiltersToRoute(): void {
        this.router.navigate([], {
            relativeTo: this.route,
            queryParams: {
                text: this.textFilter,
                orderBy: this.orderBy,
                status:this.selectedStatus.map((status) => status.text).join(','),
                reverseOrder: this.reverseOrder,
                tags: this.getSelectedTags().toString()
            }
        });
    }

    private tagPresent(tags: String[], scenario: ScenarioIndex): boolean {
        if (tags.length > 0) {
            return intersection(tags, scenario.tags).length > 0;
        } else {
            return true;
        }
    }

    private getSelectedTags() {
        return this.selectedTags.map((i) => i.text);
    }
}
