/*
 *  Copyright 2017-2024 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
