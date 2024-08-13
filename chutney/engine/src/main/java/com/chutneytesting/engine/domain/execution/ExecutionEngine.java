/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.engine.domain.execution;

import com.chutneytesting.engine.domain.execution.engine.Dataset;
import com.chutneytesting.engine.domain.execution.engine.Environment;

public interface ExecutionEngine {

    Long execute(StepDefinition stepDefinition, Dataset dataset, ScenarioExecution execution, Environment environment);

    void shutdown();
}
