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

import static org.assertj.core.api.Assertions.assertThat;

import com.chutneytesting.server.core.domain.execution.history.ExecutionHistory.ExecutionSummary;
import com.chutneytesting.server.core.domain.execution.history.ImmutableExecutionHistory;
import com.chutneytesting.server.core.domain.execution.report.ServerReportStatus;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PurgeExecutionsFiltersTest {

    public static Stream<Arguments> isExecutionBeforeGivenMilliseconds() {
        return Stream.of(
            Arguments.of(LocalDateTime.now(), 10000, false, 50, false),
            Arguments.of(LocalDateTime.now().minus(500, ChronoUnit.MILLIS), 100, true, 500, true),
            Arguments.of(LocalDateTime.now().plus(1000, ChronoUnit.MILLIS), 200, false, 1000, true)
        );
    }

    @ParameterizedTest
    @MethodSource("isExecutionBeforeGivenMilliseconds")
    void process_execution_predicate_before_given_milliseconds(
        LocalDateTime executionDate,
        int nowOffsetMillis,
        Boolean expectedTestResult,
        int sleep,
        Boolean expectedTestResultAfterSleep
    ) throws InterruptedException {
        // Given
        ImmutableExecutionHistory.ExecutionSummary execution = ImmutableExecutionHistory.ExecutionSummary.builder()
            .duration(0)
            .status(ServerReportStatus.NOT_EXECUTED)
            .testCaseTitle("")
            .environment("")
            .user("")
            .executionId(0L)
            .scenarioId("")
            .time(executionDate)
            .build();

        // When
        Predicate<ExecutionSummary> sut = PurgeExecutionsFilters.isExecutionDateBeforeNowMinusOffset(
            ExecutionSummary::time,
            nowOffsetMillis
        );

        // Then
        assertThat(sut.test(execution)).isEqualTo(expectedTestResult);
        Thread.sleep(sleep);
        assertThat(sut.test(execution)).isEqualTo(expectedTestResultAfterSleep);
    }
}
