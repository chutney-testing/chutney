/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.execution.api;

import java.util.Map;
import java.util.Set;

public record ScenarioExecutionReportDto(
    long executionId,
    String scenarioName,
    String environment,
    String user,
    Set<String> tags,
    Map<String, Object> contextVariables,
    StepExecutionReportCoreDto report
) {
}
