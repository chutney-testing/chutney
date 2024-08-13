/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.junit.engine;

import com.chutneytesting.ExecutionConfiguration;
import com.chutneytesting.engine.api.execution.EnvironmentDto;
import com.chutneytesting.engine.api.execution.StepDefinitionDto;
import com.chutneytesting.engine.api.execution.StepExecutionReportDto;
import com.chutneytesting.glacio.api.ExecutionRequestMapper;
import io.reactivex.rxjava3.core.Observable;
import org.junit.platform.engine.support.hierarchical.EngineExecutionContext;

public class ChutneyEngineExecutionContext implements EngineExecutionContext {

    private final ExecutionConfiguration executionConfiguration;
    private final EnvironmentDto environment;

    protected ChutneyEngineExecutionContext(ExecutionConfiguration executionConfiguration, EnvironmentDto environment) {
        this.executionConfiguration = executionConfiguration;
        this.environment = environment;
    }

    protected Observable<StepExecutionReportDto> executeScenario(StepDefinitionDto stepDefinitionDto) {
        Long executionId = executionConfiguration.embeddedTestEngine().executeAsync(ExecutionRequestMapper.toDto(stepDefinitionDto, environment));
        return executionConfiguration.embeddedTestEngine().receiveNotification(executionId);
    }

}
