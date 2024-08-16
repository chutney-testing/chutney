/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.engine.domain.execution.engine.step;

import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;
import static java.util.Optional.ofNullable;

import com.chutneytesting.engine.domain.execution.engine.evaluation.EvaluationException;
import com.chutneytesting.engine.domain.execution.engine.scenario.ScenarioContext;
import com.chutneytesting.engine.domain.execution.engine.scenario.ScenarioContextImpl;
import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import java.util.Map;

class StepContext {

    private final ScenarioContext scenarioContext;
    private final Map<String, Object> localContext;
    private final Map<String, Object> evaluatedInputs;
    private final Map<String, Object> stepOutputs;
    private StepContextSnapshot stepContextSnapshot;

    StepContext() {
        this(new ScenarioContextImpl(), new LinkedHashMap<>(), new LinkedHashMap<>());
    }

    StepContext(ScenarioContext scenarioContext, Map<String, Object> localContext, Map<String, Object> evaluatedInputs) throws EvaluationException {
        this(scenarioContext, localContext, evaluatedInputs, new LinkedHashMap<>());
    }

    private StepContext(ScenarioContext scenarioContext, Map<String, Object> localContext, Map<String, Object> evaluatedInputs, Map<String, Object> stepOutputs) {
        this.scenarioContext = scenarioContext;
        this.localContext = localContext;
        this.evaluatedInputs = evaluatedInputs;
        this.stepOutputs = stepOutputs;
        this.stepContextSnapshot = new StepContextSnapshot();
    }

    private StepContext copySnapshotsInputOutput() {
        this.stepContextSnapshot = new StepContextSnapshot(evaluatedInputs, stepOutputs);
        return this;
    }

    public StepContextSnapshot getStepContextSnapshot() {
        return stepContextSnapshot;
    }

    Map<String, Object> evaluationContext() {
        final Map<String, Object> allResults = Maps.newLinkedHashMap(scenarioContext);
        allResults.putAll(localContext);
        allResults.putAll(stepOutputs);
        return allResults;
    }

    ScenarioContext getScenarioContext() {
        return scenarioContext;
    }

    Map<String, Object> getEvaluatedInputs() {
        return ofNullable(evaluatedInputs).orElse(emptyMap());
    }

    void addStepOutputs(Map<String, Object> stepOutputs) {
        if (stepOutputs != null) {
            this.stepOutputs.putAll(stepOutputs);
        }
    }

    void addScenarioContext(Map<String, Object> context) {
        if (context != null) {
            this.scenarioContext.putAll(context);
        }
    }

    Map<String, Object> getStepOutputs() {
        return ofNullable(stepOutputs).orElse(emptyMap());
    }

    StepContext copy() {
        return new StepContext(scenarioContext.unmodifiable(), unmodifiableMap(localContext), unmodifiableMap(evaluatedInputs), unmodifiableMap(stepOutputs)).copySnapshotsInputOutput();
    }
}
