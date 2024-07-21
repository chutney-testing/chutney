/*
 *  Copyright 2017-2023 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.chutneytesting.engine.domain.execution.engine.step;

import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;
import static java.util.Optional.ofNullable;

import com.chutneytesting.engine.domain.execution.engine.evaluation.EvaluationException;
import com.chutneytesting.engine.domain.execution.engine.scenario.ScenarioContext;
import com.chutneytesting.engine.domain.execution.engine.scenario.ScenarioContextImpl;
import com.chutneytesting.engine.domain.execution.engine.step.jackson.ReportObjectMapperConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import java.util.HashMap;
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
