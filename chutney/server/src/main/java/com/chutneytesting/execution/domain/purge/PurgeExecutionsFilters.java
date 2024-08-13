/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.execution.domain.purge;

import com.chutneytesting.server.core.domain.execution.history.ExecutionHistory;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;
import java.util.function.Predicate;

class PurgeExecutionsFilters {
    static Predicate<ExecutionHistory.ExecutionSummary> isScenarioExecutionLinkedWithCampaignExecution = es -> es.campaignReport().isEmpty();

    static <Execution> Predicate<Execution> isExecutionDateBeforeNowMinusOffset(
        Function<Execution, LocalDateTime> executionDateFunction,
        int nowOffsetMillis
    ) {
        if (nowOffsetMillis <= 0) {
            return e -> true;
        } else {
            return exec -> {
                LocalDateTime now = LocalDateTime.now().minus(nowOffsetMillis, ChronoUnit.MILLIS);
                return executionDateFunction.apply(exec).isBefore(now);
            };
        }
    }
}
