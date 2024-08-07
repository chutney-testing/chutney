/*
 * Copyright 2017-2024 Enedis
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

package com.chutneytesting.engine.api.execution;

import io.reactivex.rxjava3.core.Observable;

/**
 * Dont forget to use autocloseable resource :
 * <pre>
 *  try(testEngine) {
 *    testEngine.execute(executionRequest)
 *    ....
 *  }
 * </pre>
 */
public interface TestEngine extends AutoCloseable {

    StepExecutionReportDto execute(ExecutionRequestDto request);

    Long executeAsync(ExecutionRequestDto request);

    Observable<StepExecutionReportDto> receiveNotification(Long executionId);

    void pauseExecution(Long executionId);

    void resumeExecution(Long executionId);

    void stopExecution(Long executionId);
}
