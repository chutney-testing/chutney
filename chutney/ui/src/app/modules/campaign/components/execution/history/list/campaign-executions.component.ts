/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Component, EventEmitter, Inject, Input, OnChanges, OnDestroy, Output, SimpleChanges } from '@angular/core';
import { Campaign, CampaignExecutionReport, CampaignReport } from '@model';
import { Params } from '@angular/router';
import { ExecutionStatus } from '@core/model/scenario/execution-status';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { debounceTime, map, tap } from 'rxjs/operators';
import { Subscription } from 'rxjs';
import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { DateFormatPipe } from 'ngx-moment';
import { NgbDate } from '@ng-bootstrap/ng-bootstrap/datepicker/ngb-date';
import { TranslateService } from '@ngx-translate/core';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { DROPDOWN_SETTINGS } from '@core/model/dropdown-settings';
import { ListItem } from 'ng-multiselect-dropdown/multiselect.model';
import { DatasetUtils } from "@shared/tools/dataset-utils";

@Component({
    selector: 'chutney-campaign-executions',
    templateUrl: './campaign-executions.component.html',
    styleUrls: ['./campaign-executions.component.scss']
})
export class CampaignExecutionsComponent implements OnChanges, OnDestroy {

    @Input() executions: CampaignReport[] = [];
    @Input() campaign: Campaign;
    @Output() onExecutionSelect = new EventEmitter<{ execution: CampaignReport, focus: boolean }>();
    @Input() filters: Params;
    @Output() filtersChange = new EventEmitter<Params>();

    ExecutionStatus = ExecutionStatus;

    filtersForm: FormGroup;
    private filters$: Subscription;
    filteredExecutions: CampaignReport[] = [];

    status: ListItem[] = [];

    environments: ListItem[] = [];

    datasets: ListItem[] = [];

    executors: ListItem[] = [];

    private readonly iso_Date_Delimiter = '-';

    constructor(private formBuilder: FormBuilder,
                private datePipe: DateFormatPipe,
                private translateService: TranslateService,
                private datasetUtils: DatasetUtils,
                @Inject(DROPDOWN_SETTINGS) public dropdownSettings: IDropdownSettings) {
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.initFiltersOptions();
        this.applyFilters();
        this.onFiltersChange();
    }

    ngOnDestroy(): void {
        this.filters$.unsubscribe();
    }

    getDateFilterValue() {
        let date: NgbDateStruct = this.filtersForm.value.date;
        return new Date(date.year, date.month - 1, date.day);
    }

    noExecutionAt() {
        return (date: NgbDate) => !this.executions.filter(exec => this.matches(exec.report, {date: date})).length;
    }

    openReport(execution: CampaignReport, focus: boolean = true) {
        this.onExecutionSelect.emit({execution, focus});
    }

    getFormControl(name: string): FormControl {
        return this.filtersForm.get(name) as FormControl;
    }

    private initFiltersOptions() {
        this.status = [...new Set(this.executions.map(exec => exec.report.status))].map(status => this.toSelectOption(status,  this.translateService.instant(ExecutionStatus.toString(status))));
        this.environments = [...new Set(this.executions.map(exec => exec.report.executionEnvironment))].map(env => this.toSelectOption(env));
        this.datasets = this.removeDuplicateListItems(this.executions.map(exec => exec.report.dataset).map(dataset => dataset ? this.toSelectOption(dataset.id ? dataset.id : "Custom") : null).filter(ds => ds));
        this.executors = [...new Set(this.executions.map(exec => exec.report.user))].map(user => this.toSelectOption(user));
    }

    private applyFilters() {
        this.applyFiltersOnHeaders();
        this.applyFiltersOnExecutions();
    }

    private applyFiltersOnHeaders() {
        this.filtersForm = this.formBuilder.group({
            keyword: this.filters['keyword'],
            date: this.formBuilder.control(this.toNgbDate(this.filters['date'])),
            status: this.formBuilder.control(this.selectedOptionsFromUri(this.filters['status'],  (status) => this.translateService.instant(ExecutionStatus.toString(status)))),
            environments: this.formBuilder.control(this.selectedOptionsFromUri(this.filters['env'])),
            dataset: this.formBuilder.control(this.selectedOptionsFromUri(this.filters['dataset'])),
            executors: this.formBuilder.control(this.selectedOptionsFromUri(this.filters['exec']))
        });
    }

    private applyFiltersOnExecutions() {
        this.filteredExecutions = this.executions.filter(exec => this.matches(exec.report, this.filtersForm.value))
    }

    protected getDataset(execution: CampaignReport) {
        return this.datasetUtils.getDatasetName(execution.report.dataset)
    }

    private onFiltersChange() {
        this.filters$ = this.filtersForm
            .valueChanges
            .pipe(
                debounceTime(500),
                map(value => this.toQueryParams(value)),
                tap(params => this.filtersChange.emit(params))
            ).subscribe();
    }

    private selectedOptionsFromUri(param: string, labelResolver?: (param) => string) {
        if (param) {
            return param
                .split(',')
                .map(part => this.toSelectOption(part, labelResolver ? labelResolver(part) : part));
        }
        return [];
    }

    private toSelectOption(id: string, label: string = id) {
        return {id: id, text: label };
    }

    private toQueryParams(filters: any): Params {
        const params: Params = {};
        if (filters.keyword) {
            params['keyword'] = filters.keyword;
        }
        if (filters.status && filters.status.length) {
            params['status'] = filters.status.map(status => status.id).toString();
        }
        if (filters.date) {
            params['date'] = this.toIsoDate(filters.date);
        }
        if (filters.environments && filters.environments.length) {
            params['env'] = filters.environments.map(env => env.id).toString();
        }
        if (filters.dataset && filters.dataset.length) {
            params['dataset'] = filters.dataset.map(dataset => dataset.id).toString();
        }
        if (filters.executors && filters.executors.length) {
            params['exec'] = filters.executors.map(env => env.id).toString();
        }
        return params;
    }

    private toIsoDate(ngbDate: NgbDateStruct) {
        let dd = String(ngbDate.day).padStart(2, '0');
        let mm = String(ngbDate.month).padStart(2, '0')
        let yyyy = ngbDate.year;
        return [yyyy, mm, dd].join(this.iso_Date_Delimiter);
    }

    private toNgbDate(isoString: string) {
        if (isoString) {
            const date = isoString.split('-');
            return {
                day: parseInt(date[2], 10),
                month: parseInt(date[1], 10),
                year: parseInt(date[0], 10)
            };
        }
        return null;
    }

    private matches(report: CampaignExecutionReport, filters: any): boolean {
        let keywordMatch = true;
        if (filters.keyword) {
            let space = ' ';
            let searchScope = report.user
                + space
                + report.executionEnvironment
                + space
                + report.dataset
                + space
                + this.datePipe.transform(report.startDate, 'DD MMM. YYYY HH:mm')
                + space
                + report.executionId
                + space
                + this.translateService.instant(ExecutionStatus.toString(report.status))
                + space;

            keywordMatch = searchScope.toLowerCase().includes(filters.keyword.toLowerCase());
        }

        let statusMatch = true;
        if (filters.status && filters.status.length) {
            statusMatch = !!filters.status.find(status => status.id === report.status);
        }

        let dateMatch = true;
        if (filters.date) {
            const dateFilter = new Date(filters.date.year, filters.date.month - 1, filters.date.day);
            dateMatch = dateFilter.toDateString() === new Date(report.startDate).toDateString();
        }

        let userMatch = true;
        if (filters.executors && filters.executors.length) {
            userMatch = !!filters.executors.find(executor => executor.id === report.user);
        }

        let envMatch = true;
        if (filters.environments && filters.environments.length) {
            envMatch = !!filters.environments.find(env => env.id === report.executionEnvironment);
        }

        let datasetMatch = true;
        if (filters.dataset && filters.dataset.length) {
            datasetMatch = !!filters.dataset.find(dataset => this.datasetUtils.getDatasetName(dataset) == this.datasetUtils.getDatasetName(report.dataset));
        }

        return keywordMatch && statusMatch && dateMatch && userMatch && envMatch && datasetMatch;
    }

    private removeDuplicateListItems(list: ListItem[]) {
        const seen = new Set<string>();
        return list.filter(elem => {
            if (!elem) return false
            const key = `${elem.text}-${elem.id}`
            if (seen.has(key)) {
                return false;
            }
            seen.add(key);
            return true;
        });
    }
}
