/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.execution;

import com.chutneytesting.server.core.domain.execution.report.StepExecutionReportCore;
import io.reactivex.rxjava3.core.Observable;
import org.apache.commons.lang3.tuple.Pair;

public interface ServerTestEngine {

    StepExecutionReportCore execute(ExecutionRequest executionRequest);

    Pair<Observable<StepExecutionReportCore>, Long> executeAndFollow(ExecutionRequest executionRequest);

    void stop(Long executionHash);

    void pause(Long executionHash);

    void resume(Long executionHash);
}
