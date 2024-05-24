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

import { ScenarioExecutionService } from '@modules/scenarios/services/scenario-execution.service';
import { environment } from '@env/environment';
import { of } from 'rxjs';
import { HttpClient } from '@angular/common/http'; // replace with your service's path

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
        const dataset = 'testDataset';

        service.executeScenarioAsync(scenarioId, env, dataset)
            .subscribe({
                    next: execId => expect(execId).withContext('expected exec id').toEqual('123',),
                    error: fail
                }
            );
        expect(httpClientSpy.post.calls.count()).withContext('expected one call').toBe(1, );
        let expectedCallPath = `${environment.backend}${service.resourceUrl}/executionasync/v1/${scenarioId}/${env}/${dataset}`;
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

