/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
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
