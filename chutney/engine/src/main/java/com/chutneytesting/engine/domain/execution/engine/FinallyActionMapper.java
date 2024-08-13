/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.engine.domain.execution.engine;

import static java.util.Collections.emptyMap;

import com.chutneytesting.action.spi.FinallyAction;
import com.chutneytesting.engine.domain.environment.TargetImpl;
import com.chutneytesting.engine.domain.execution.StepDefinition;
import com.chutneytesting.engine.domain.execution.strategies.StepStrategyDefinition;
import com.chutneytesting.engine.domain.execution.strategies.StrategyProperties;

class FinallyActionMapper {

    StepDefinition toStepDefinition(FinallyAction finallyAction) {
        return new StepDefinition(
            finallyAction.name(),
            finallyAction.target()
                .orElse(TargetImpl.NONE),
            finallyAction.type(),
            finallyAction.strategyType()
                .map(st -> new StepStrategyDefinition(st, new StrategyProperties(finallyAction.strategyProperties().orElse(emptyMap()))))
                .orElse(null),
            finallyAction.inputs(),
            null,
            null,
            finallyAction.validations()
        );
    }
}
