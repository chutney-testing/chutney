/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { ScenarioExecutionService } from '@modules/scenarios/services/scenario-execution.service';
import { environment } from '@env/environment';
import { of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Dataset } from "@model";

describe('ScenarioService', () => {
    let service: ScenarioExecutionService;
    let httpClientSpy: jasmine.SpyObj<HttpClient>;

    beforeEach(() => {
        httpClientSpy = jasmine.createSpyObj('HttpClient', ['post']);
        service = new ScenarioExecutionService(httpClientSpy);
    });


    it('should execute scenario asynchronously on given env and dataset', () => {
        httpClientSpy.post.and.returnValue(of('123'));
        const scenarioId = 'testScenario';
        const env = 'testEnv';
        const dataset = new Dataset("", "", [], new Date(), [], []);

        service.executeScenarioAsync(scenarioId, env, dataset)
            .subscribe({
                    next: execId => expect(execId).withContext('expected exec id').toEqual('123',),
                    error: fail
                }
            );
        expect(httpClientSpy.post.calls.count()).withContext('expected one call').toBe(1, );
        let expectedCallPath = `${environment.backend}${service.resourceUrl}/executionasync/v1/${scenarioId}/${env}`;
        expect(httpClientSpy.post.calls.allArgs()[0][0]).withContext('expected post url').toBe(expectedCallPath);
    });

    it('should execute scenario asynchronously on given env', () => {
        httpClientSpy.post.and.returnValue(of('123'));
        const scenarioId = 'testScenario';
        const env = 'testEnv';

        service.executeScenarioAsync(scenarioId, env)
            .subscribe({
                    next: execId => expect(execId).withContext('expected exec id').toEqual('123',),
                    error: fail
                }
            );
        expect(httpClientSpy.post.calls.count()).withContext('expected one call').toBe(1, );
        let expectedCallPath = `${environment.backend}${service.resourceUrl}/executionasync/v1/${scenarioId}/${env}`;
        expect(httpClientSpy.post.calls.allArgs()[0][0]).withContext('expected post url').toBe(expectedCallPath);
    });
});

