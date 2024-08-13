/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Component } from '@angular/core';

import { Execution } from '@model';
import { DatabaseAdminService } from '@core/services';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { BehaviorSubject, map, Observable, switchMap } from 'rxjs';

@Component({
    selector: 'chutney-database-admin',
    templateUrl: './database-admin.component.html'
})
export class DatabaseAdminComponent {

    activeTabId = 1;
    // Executions search
    query: string;
    errorMessage: string;
    executions: Execution[];
    private _executionsFilters: Params = {};
    // Vaccuum
    private dbSize$ = new BehaviorSubject<number>(0);
    dbSizeObs$: Observable<number>;
    vacuumReport: number[];
    vacuumRunning = false;


    constructor(
        private databaseAdminService: DatabaseAdminService,
        private route: ActivatedRoute,
        private router: Router) {
        this.executions = [];
        this.vacuumReport = [];
        this.dbSizeObs$ = this.dbSize$.asObservable().pipe(
            switchMap(() => databaseAdminService.computeDatabaseSize()
                .pipe(
                    map((x) => x)
                )
            )
        );
    }

    get executionsFilters(): Params {
        return this._executionsFilters;
    }

    set executionsFilters(value: Params) {
        const {open, active, ...executionsParams} = value;
        this._executionsFilters = executionsParams;
        this.updateQueryParams();
    }

    private updateQueryParams() {
        let queryParams = this.cleanParams({...this.executionsFilters});
        this.router.navigate([], {
            relativeTo: this.route,
            queryParams: queryParams
        });
    }

    openReport(request: { execution: Execution }) {
        const url = this.router.serializeUrl(this.router.createUrlTree(['scenario', request.execution.scenarioId, 'executions'], {queryParams: {open: request.execution.executionId, active: request.execution.executionId}}));
        window.open('#' + url, "_blank");
    }

    private cleanParams(params: Params) {
        Object.keys(params).forEach(key => {
            if (params[key] === null || params[key] === '' || params[key] === '0') {
                delete params[key];
            }
        });
        return params;
    }

    searchQuery() {
        if (this.query == null || this.query.trim().length === 0) {
            return;
        }
        this.errorMessage = null;
        this.databaseAdminService.getExecutionReportMatchQuery(this.query)
            .subscribe({
                next: (res: Execution[]) => {
                    res?.forEach(e => e.tags.sort());
                    this.executions = res;
                },
                error: (error) => {
                    this.errorMessage = error
                }
            });
    }

    updateQuery(text: string) {
        this.query = text;
    }

    refreshDBSize() {
        this.dbSize$.next(0);
    }

    launchVacuum() {
        this.vacuumRunning = true;
        this.databaseAdminService.compactDatabase()
            .subscribe({
                next: (val: number[]) => {
                    this.vacuumReport = val;
                    this.refreshDBSize();
                },
                error: (error) => {
                    this.vacuumRunning = false;
                    this.errorMessage = ( error.error ? error.error : (error.message ? error.message : error) );
                },
                complete: () => this.vacuumRunning = false
            });
    }
}
