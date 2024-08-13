/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.execution.api;

import com.chutneytesting.server.core.domain.execution.report.ScenarioExecutionReport;
import com.chutneytesting.server.core.domain.execution.report.StepExecutionReportCore;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ScenarioExecutionReportMapper {
    ScenarioExecutionReportDto toDto(ScenarioExecutionReport source);
    StepExecutionReportCoreDto toStepDto(StepExecutionReportCore domain);
}
