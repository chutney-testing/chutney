/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
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
