/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.engine.domain.execution.engine;

import static java.util.Optional.ofNullable;

import com.chutneytesting.action.domain.ActionTemplate;
import com.chutneytesting.action.domain.ActionTemplateRegistry;
import com.chutneytesting.action.domain.parameter.ParameterResolver;
import com.chutneytesting.action.spi.Action;
import com.chutneytesting.action.spi.ActionExecutionResult;
import com.chutneytesting.action.spi.injectable.ActionsConfiguration;
import com.chutneytesting.action.spi.injectable.FinallyActionRegistry;
import com.chutneytesting.action.spi.injectable.Logger;
import com.chutneytesting.action.spi.injectable.StepDefinitionSpi;
import com.chutneytesting.action.spi.injectable.Target;
import com.chutneytesting.engine.domain.execution.ScenarioExecution;
import com.chutneytesting.engine.domain.execution.engine.parameterResolver.ContextParameterResolver;
import com.chutneytesting.engine.domain.execution.engine.parameterResolver.DelegateLogger;
import com.chutneytesting.engine.domain.execution.engine.parameterResolver.InputParameterResolver;
import com.chutneytesting.engine.domain.execution.engine.parameterResolver.TypedValueParameterResolver;
import com.chutneytesting.engine.domain.execution.engine.step.Step;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.LoggerFactory;

public class DefaultStepExecutor implements StepExecutor {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(DefaultStepExecutor.class);

    private final ActionTemplateRegistry actionTemplateRegistry;

    public DefaultStepExecutor(ActionTemplateRegistry actionTemplateRegistry) {
        this.actionTemplateRegistry = actionTemplateRegistry;
    }

    @Override
    public void execute(ScenarioExecution scenarioExecution, Target targetServer, Step step) {
        String type = step.type();

        Optional<ActionTemplate> matchedAction = actionTemplateRegistry.getByIdentifier(type);

        if (matchedAction.isPresent()) {
            List<ParameterResolver> parameterResolvers = gatherResolvers(scenarioExecution, targetServer, step);

            ActionExecutionResult executionResult;
            try {
                Action action = matchedAction.get().create(parameterResolvers);
                List<String> errors = action.validateInputs();
                if (errors.isEmpty()) {
                    executionResult = action.execute();
                    step.updateContextFrom(executionResult);
                } else {
                    step.failure(errors.toArray(new String[0]));
                }
            } catch (Exception e) {
                LOGGER.error("Cannot execute step: ", e);
                step.failure("Action [" + type + "] failed: " + ofNullable(e.getMessage()).orElse(e.toString()));
            }
        } else if (type.isEmpty()) {
            step.success();
        } else {
            step.failure("Action [" + type + "] not found");
        }

    }

    private List<ParameterResolver> gatherResolvers(ScenarioExecution scenarioExecution, Target target, Step step) {
        List<ParameterResolver> parameterResolvers = new ArrayList<>();
        parameterResolvers.add(new InputParameterResolver(step.getEvaluatedInputs()));
        parameterResolvers.add(new TypedValueParameterResolver<>(Target.class, target));
        parameterResolvers.add(new TypedValueParameterResolver<>(Logger.class, new DelegateLogger(step::addInformation, step::failure)));
        parameterResolvers.add(new TypedValueParameterResolver<>(StepDefinitionSpi.class, step.definition()));
        parameterResolvers.add(new TypedValueParameterResolver<>(FinallyActionRegistry.class, scenarioExecution::registerFinallyAction));
        parameterResolvers.add(new TypedValueParameterResolver<>(ActionsConfiguration.class, scenarioExecution.getActionsConfiguration()));
        parameterResolvers.add(new ContextParameterResolver(step.getScenarioContext()));
        return parameterResolvers;
    }
}
