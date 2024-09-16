/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Component, EventEmitter, Input, OnChanges, OnDestroy, Output, SimpleChanges, TemplateRef, ViewChild } from '@angular/core';
import { Execution, GwtTestCase } from '@model';
import { Params, Router } from '@angular/router';
import { ExecutionStatus } from '@core/model/scenario/execution-status';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { debounceTime, map, tap } from 'rxjs/operators';
import { Subscription } from 'rxjs';
import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { DateFormatPipe } from 'ngx-moment';
import { NgbDate } from '@ng-bootstrap/ng-bootstrap/datepicker/ngb-date';
import { TranslateService } from '@ngx-translate/core';
import { ListItem } from 'ng-multiselect-dropdown/multiselect.model';
import { BsModalRef, BsModalService } from 'ngx-bootstrap/modal';
import { DatasetUtils } from "@shared/tools/dataset-utils";

@Component({
    selector: 'chutney-scenario-executions',
    templateUrl: './scenario-executions.component.html',
    styleUrls: ['./scenario-executions.component.scss']
})
export class ScenarioExecutionsComponent implements OnChanges, OnDestroy {
    ExecutionStatus = ExecutionStatus;
    filteredExecutions: Execution[] = [];
    filtersForm: FormGroup;

    status: ListItem[] = [];
    environments: ListItem[] = [];
    datasets: ListItem[] = [];
    executors: ListItem[] = [];
    campaigns: ListItem[] = [];
    tags: ListItem[] = [];
    selectSettings = {
        text: '',
        enableCheckAll: false,
        enableSearchFilter: true,
        autoPosition: false,
        classes: 'dropdown-list1'
    };

    private filters$: Subscription;
    private executionIdToDelete :number = null;

    private readonly iso_Date_Delimiter = '-';

    @Input() executions: Execution[] = [];
    @Input() scenario: GwtTestCase;
    @Output() onExecutionSelect = new EventEmitter<{ execution: Execution, focus: boolean }>();
    @Input() filters: Params;
    @Output() filtersChange = new EventEmitter<Params>();
    @Output() onReplay = new EventEmitter<number>();
    @Output() onDelete = new EventEmitter<number>();

    @ViewChild('delete_modal') deleteModal: TemplateRef<any>;
    modalRef: BsModalRef;

    constructor(private router: Router,
                private formBuilder: FormBuilder,
                private datePipe: DateFormatPipe,
                private translateService: TranslateService,
                private modalService: BsModalService,
                private datasetUtils: DatasetUtils) {
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.initFiltersOptions();
        this.applyFilters();
        this.onFiltersChange();
    }

    getDateFilterValue() {
        let date: NgbDateStruct = this.filtersForm.value.date;
        return new Date(date.year, date.month - 1, date.day);
    }

    noExecutionAt() {
        return (date: NgbDate) => !this.executions.filter(exec => this.matches(exec, {date: date})).length;
    }

    openReport(execution: Execution, focus: boolean = true) {
        this.onExecutionSelect.emit({execution, focus});
    }

    ngOnDestroy(): void {
        this.filters$.unsubscribe();
    }

    private initFiltersOptions() {
        this.status = [...new Set(this.executions.map(exec => exec.status))].map(status => this.toSelectOption(status, this.translateService.instant(ExecutionStatus.toString(status))));
        this.environments = [...new Set(this.executions.map(exec => exec.environment))].map(env => this.toSelectOption(env));
        this.datasets = this.removeDuplicateListItems(this.executions.map(exec => exec.dataset).filter(ds=> !!ds).map(ds => ds.id ? ds.id : "Custom").map(ds => this.toSelectOption(ds)));
        this.executors = [...new Set(this.executions.map(exec => exec.user))].map(user => this.toSelectOption(user));
        this.campaigns = [...new Set(this.executions.filter(exec => !!exec.campaignReport).map(exec => exec.campaignReport.campaignName))].map(camp => this.toSelectOption(camp));
        this.tags = [...new Set(this.executions.flatMap(exec => exec.tags))].map(tag => this.toSelectOption(tag));
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
            datasets: this.formBuilder.control(this.selectedOptionsFromUri(this.filters['datasets'])),
            executors: this.formBuilder.control(this.selectedOptionsFromUri(this.filters['exec'])),
            campaigns: this.formBuilder.control(this.selectedOptionsFromUri(this.filters['camp'])),
            tags: this.formBuilder.control(this.selectedOptionsFromUri(this.filters['tags'])),
        });
    }

    private applyFiltersOnExecutions() {
        this.filteredExecutions = this.executions.filter(exec => this.matches(exec, this.filtersForm.value))
    }

    private onFiltersChange() {
        this.filters$ = this.filtersForm
            .valueChanges
            .pipe(
                debounceTime(500),
                map(value => this.toQueryParams(value)),
                tap(params => this.filtersChange.emit(params)))
            .subscribe();
    }

    protected getDatasetFromExecution(execution: Execution) {
        return this.datasetUtils.getDatasetName(execution.dataset)
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
            params['status'] = filters.status.map((status: ListItem) => status.id).toString();
        }
        if (filters.date) {
            params['date'] = this.toIsoDate(filters.date);
        }
        if (filters.environments && filters.environments.length) {
            params['env'] = filters.environments.map((env: ListItem) => env.id).toString();
        }
        if (filters.datasets && filters.datasets.length) {
            params['datasets'] = filters.datasets.map((ds:ListItem) => ds.id).toString();
        }
        if (filters.campaigns && filters.campaigns.length) {
            params['camp'] = filters.campaigns.map((campaign:ListItem) => campaign.id).toString();
        }
        if (filters.executors && filters.executors.length) {
            params['exec'] = filters.executors.map((executor: ListItem) => executor.id).toString();
        }
        if (filters.tags && filters.tags.length) {
            params['tags'] = filters.tags.map((tag: ListItem) => tag.id).toString();
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

    private matches(exec: Execution, filters: any): boolean {
        let keywordMatch = true;
        if (filters.keyword) {
            let space = ' ';
            let searchScope = exec.user
                + space
                + exec.environment
                + space
                + this.datePipe.transform(exec.time, 'DD MMM. YYYY HH:mm')
                + space
                + exec.executionId
                + space
                + this.translateService.instant(ExecutionStatus.toString(exec.status))
                + space
                + exec.tags.join(space)
                + space;
            if (exec.campaignReport) {
                searchScope += space + exec.campaignReport.campaignName;
            }

            if (exec.error) {
                searchScope += space + exec.error;
            }
            keywordMatch = searchScope.toLowerCase().includes(filters.keyword.toLowerCase());
        }
        let statusMatch = true;
        if (filters.status && filters.status.length) {
            statusMatch = !!filters.status.find(status => status.id === exec.status);
        }
        let dateMatch = true;
        if (filters.date) {
            const dateFilter = new Date(filters.date.year, filters.date.month - 1, filters.date.day);
            dateMatch = dateFilter.toDateString() === new Date(exec.time).toDateString();
        }

        let userMatch = true;
        if (filters.executors && filters.executors.length) {
            userMatch = !!filters.executors.find(executor => executor.id === exec.user);
        }

        let envMatch = true;
        if (filters.environments && filters.environments.length) {
            envMatch = !!filters.environments.find(env => env.id === exec.environment);
        }

        let datasetMatch = true;
        if (filters.datasets && filters.datasets.length) {
            datasetMatch = !!filters.datasets.find((ds:ListItem) => exec.dataset !! && ds.id === exec.dataset.id);
        }

        let campaignMatch = true;
        if (filters.campaigns && filters.campaigns.length) {
            campaignMatch = !!filters.campaigns.find(camp => exec.campaignReport && camp.id === exec.campaignReport.campaignName);
        }
        let tagMatch = true;
        if (filters.tags && filters.tags.length) {
            tagMatch = !!filters.tags.find(tag => exec.tags && exec.tags.includes(tag.id));
        }

        return keywordMatch && statusMatch && dateMatch && userMatch && envMatch && datasetMatch && campaignMatch && tagMatch;
    }

    openCampaignExecution(execution: Execution, event: MouseEvent) {
        if (execution.campaignReport) {
            event.stopPropagation();
            this.router.navigate(['/campaign', execution.campaignReport.campaignId, 'executions'], {queryParams: {open: execution.campaignReport.executionId, active: execution.campaignReport.executionId}});
        }
    }

    getFormControl(name: string): FormControl {
        return this.filtersForm.get(name) as FormControl;
    }

    replay(execution: Execution, event: MouseEvent) {
        event.stopPropagation();
        this.onReplay.emit(execution.executionId);
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

    emitDeleteExecutionEvent() {
        this.onDelete.emit(this.executionIdToDelete);
    }

    openDeleteModal(executionId: number, event: MouseEvent) {
        event.stopPropagation();
        this.executionIdToDelete = executionId;
        this.modalRef = this.modalService.show(this.deleteModal, { class: 'modal-sm' });
    }

    confirm(): void {
        this.modalRef.hide();
        this.emitDeleteExecutionEvent();
    }

    decline(): void {
        this.modalRef.hide();
        this.executionIdToDelete = null;
    }
}
