/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.glacio.domain.parser.business;

import com.chutneytesting.engine.api.execution.StepDefinitionDto;
import com.chutneytesting.glacio.domain.parser.ParsingContext;
import com.github.fridujo.glacio.model.Step;
import java.util.List;

public interface IParseBusinessStep {

    /** TODO put description here **/
    StepDefinitionDto mapToStepDefinition(ParsingContext context, Step step, List<StepDefinitionDto> subSteps, StepDefinitionDto.StepStrategyDefinitionDto stepStrategyDefinition);

}
