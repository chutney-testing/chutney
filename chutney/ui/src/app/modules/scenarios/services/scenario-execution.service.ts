/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { environment } from '@env/environment';
import {Dataset, Execution, KeyValue, ScenarioExecutionReport} from '@model';
import { HttpClient } from '@angular/common/http';
import {ExternalDataset} from "@core/model/external-dataset.model";

@Injectable()
export class ScenarioExecutionService {

    resourceUrl = '/api/ui/scenario';

    constructor(private http: HttpClient) {
    }

    findScenarioExecutions(scenarioId: string): Observable<Execution[]> {
        return this.http.get<Execution[]>(environment.backend + `${this.resourceUrl}/${scenarioId}/execution/v1`)
            .pipe(
                map((res: Execution[]) => {
                    return res.map((execution) => Execution.deserialize(execution));
        }));
    }

    findScenarioExecutionSummary(executionId: number): Observable<Execution> {
        return this.http.get<Execution>(environment.backend + `${this.resourceUrl}/execution/${executionId}/summary/v1`)
            .pipe(
                map((res: Execution) => Execution.deserialize(res)));
    }

    executeScenarioAsync(scenarioId: string, env: string, dataset: Dataset = null): Observable<string> {
        const envPathParam = !!env ? `/${env}` : '';
        return this.http.post<string>(environment.backend + `${this.resourceUrl}/executionasync/v1/${scenarioId}${envPathParam}`, dataset ? dataset : {});
    }

    observeScenarioExecution(scenarioId: string, executionId: number): Observable<ScenarioExecutionReport> {
        return this.createScenarioExecutionObservable(environment.backend +
            `${this.resourceUrl}/executionasync/v1/${scenarioId}/execution/${executionId}`);
    }

    findExecutionReport(scenarioId: string, executionId: number): Observable<ScenarioExecutionReport> {
        return this.http.get<ScenarioExecutionReport>(environment.backend + `${this.resourceUrl}/${scenarioId}/execution/${executionId}/v1`)
            .pipe(map((res: Object) => {
                if (res != null && res !== '') {
                    return this.buildExecutionReport(res);
                }
                return null
            }));
    }

    stopScenario(scenarioId: string, executionId: number): Observable<void> {
        return this.http.post(environment.backend +
            `${this.resourceUrl}/executionasync/v1/${scenarioId}/execution/${executionId}/stop`, {}).pipe(map((res: Response) => {
        }));
    }

    pauseScenario(scenarioId: string, executionId: number): Observable<void> {
        return this.http.post(environment.backend +
            `${this.resourceUrl}/executionasync/v1/${scenarioId}/execution/${executionId}/pause`, {}).pipe(map((res: Response) => {
        }));
    }

    resumeScenario(scenarioId: string, executionId: number): Observable<void> {
        return this.http.post(environment.backend +
            `${this.resourceUrl}/executionasync/v1/${scenarioId}/execution/${executionId}/resume`, {}).pipe(map((res: Response) => {
        }));
    }

    deleteExecution(executionId: number): Observable<Object> {
        return this.http.delete(environment.backend + 
            `${this.resourceUrl}/execution/${executionId}`);
    }

    private createScenarioExecutionObservable(url: string) {
        return new Observable<ScenarioExecutionReport>(obs => {
            let es;
            try {
                es = new EventSource(url);
                es.onerror = () => obs.error('Error loading execution');
                es.addEventListener('partial', (evt: any) => {
                    obs.next(this.buildExecutionReportFromEvent(JSON.parse(evt.data)));
                });
                es.addEventListener('last', (evt: any) => {
                    obs.complete();
                });
            } catch (error) {
                obs.error('Error creating source event');
            }

            return () => {
                if (es) {
                    es.close();
                }
            };
        });
    }

    private buildExecutionReport(jsonResponse: any): ScenarioExecutionReport {
        let report = null;
        let contextVariables;
        let constants;
        let datatable;
        if(jsonResponse?.report) {
            let parse = JSON.parse(jsonResponse.report);
            report = parse.report;
            contextVariables = parse.contextVariables;
            constants = parse.constants &&  Object.keys(parse.constants).map(key => new KeyValue(key,parse.constants[key]));
            datatable = parse.datatable?.map(line => Object.keys(line).map(key => new KeyValue(key, line[key])))
        }
        return new ScenarioExecutionReport(
            jsonResponse.executionId,
            jsonResponse.status ? jsonResponse.status : report?.status,
            jsonResponse.duration ? jsonResponse.duration : report?.duration,
            new Date(jsonResponse.time ? jsonResponse.time : report?.startDate),
            report,
            jsonResponse.environment,
            jsonResponse.user,
            jsonResponse.testCaseTitle,
            jsonResponse.error,
            contextVariables,
            constants,
            datatable
        );
    }

    private buildExecutionReportFromEvent(jsonResponse: any): ScenarioExecutionReport {
        return new ScenarioExecutionReport(
            jsonResponse.executionId,
            jsonResponse.status ? jsonResponse.status : jsonResponse.report?.status,
            jsonResponse.duration ? jsonResponse.duration : jsonResponse.report?.duration,
            new Date(jsonResponse.time ? jsonResponse.time : jsonResponse.report?.startDate),
            jsonResponse.report,
            jsonResponse.environment,
            jsonResponse.user,
            jsonResponse.scenarioName
        );
    }
}
