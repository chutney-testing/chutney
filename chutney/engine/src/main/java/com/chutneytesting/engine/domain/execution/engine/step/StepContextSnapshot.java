/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.engine.domain.execution.engine.step;

import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;

import com.chutneytesting.engine.domain.execution.engine.step.jackson.ReportObjectMapperConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.HashMap;
import java.util.Map;

class StepContextSnapshot {
    private final Map<String, Object> inputsSnapshot;
    private final Map<String, Object> outputsSnapshot;

    public StepContextSnapshot() {
        this.inputsSnapshot = emptyMap();
        this.outputsSnapshot = emptyMap();
    }

    public StepContextSnapshot(Map<String, Object> inputsSnapshot, Map<String, Object> outputsSnapshot) {
        this.inputsSnapshot = mapStringObjectToString(inputsSnapshot);
        this.outputsSnapshot = mapStringObjectToString(outputsSnapshot);
    }

    public Map<String, Object> getInputsSnapshot() {
        return unmodifiableMap(inputsSnapshot);
    }

    public Map<String, Object> getOutputsSnapshot() {
        return unmodifiableMap(outputsSnapshot);
    }

    private Map<String, Object> mapStringObjectToString(Map<String, Object> originalMap) {
        Map<String, Object> stringMap = new HashMap<>();
        originalMap.forEach((key, value) -> {
            try {
                String stringObject = ReportObjectMapperConfiguration.reportObjectMapper().writeValueAsString(value);
                Object jsonObject = ReportObjectMapperConfiguration.reportObjectMapper().readTree(stringObject);
                stringMap.put(key, jsonObject);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        return stringMap;
    }
}
