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

import static java.util.Collections.EMPTY_MAP;
import static java.util.Optional.ofNullable;

import com.chutneytesting.engine.domain.execution.report.Status;
import com.chutneytesting.engine.domain.execution.report.StepExecutionReport;
import java.util.Map;
import java.util.stream.Collectors;

class StepExecutionReportMapper {

    private StepExecutionReportMapper() {
    }

    static StepExecutionReportDto toDto(StepExecutionReport report) {
        return new StepExecutionReportDto(
            report.name,
            report.environment,
            report.startDate,
            report.duration,
            StatusMapper.toDto(report.status),
            report.information,
            report.errors,
            report.steps.stream().map(StepExecutionReportMapper::toDto).collect(Collectors.toList()),
            StepContextMapper.toDto(report.scenarioContext, report.evaluatedInputsSnapshot, report.stepResultsSnapshot),
            report.type,
            report.targetName,
            report.targetUrl,
            report.strategy
        );
    }

    static class StepContextMapper {

        @SuppressWarnings("unchecked")
        static StepExecutionReportDto.StepContextDto toDto(Map<String, Object> scenarioContext, Map<String, Object> evaluatedInputSnapshot, Map<String, Object> stepResultsSnapshot) {
            return new StepExecutionReportDto.StepContextDto(
                ofNullable(scenarioContext).orElse(EMPTY_MAP),
                ofNullable(evaluatedInputSnapshot).orElse(EMPTY_MAP),
                ofNullable(stepResultsSnapshot).orElse(EMPTY_MAP)
            );
        }

    }

    static class StatusMapper {
        static StatusDto toDto(Status status) {
            return StatusDto.valueOf(status.name());
        }
    }
}
