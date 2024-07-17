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

package com.chutneytesting.execution.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.chutneytesting.engine.domain.execution.engine.step.Step;
import com.chutneytesting.server.core.domain.execution.report.ScenarioExecutionReport;
import com.chutneytesting.server.core.domain.execution.report.StepExecutionReportCore;
import com.chutneytesting.server.core.domain.execution.report.StepExecutionReportCoreBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class ScenarioExecutionTest {

    @Test
    public void should_serialize_and_deserialize_simple_input_and_output_in_report() throws JsonProcessingException {
        // Given
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        Map<String, Object> mapStringObject = Map.of("TUTU", "TITI");

        Step.StepContextSnapshot stepContextSnapshot = new Step.StepContextSnapshot(mapStringObject, mapStringObject, objectMapper);
        StepExecutionReportCore stepExecutionReport = new StepExecutionReportCoreBuilder()
            .setStepOutputs(new HashMap<>(stepContextSnapshot.getOutputsSnapshot()))
            .setEvaluatedInputs(new HashMap<>(stepContextSnapshot.getInputsSnapshot()))
            .createStepExecutionReport();
        ScenarioExecutionReport scenarioExecutionReport = new ScenarioExecutionReport(1L, "", "", "", List.of(), stepExecutionReport);

        // When
        String serializedReport = objectMapper.writeValueAsString(scenarioExecutionReport);

        // Then
        assertThat(serializedReport).isEqualTo("{\"executionId\":1,\"scenarioName\":\"\",\"environment\":\"\",\"user\":\"\",\"tags\":[],\"constants\":{},\"datatable\":[],\"report\":{\"name\":null,\"duration\":0,\"startDate\":null,\"status\":null,\"information\":[],\"errors\":[],\"steps\":[],\"type\":null,\"targetName\":\"\",\"targetUrl\":\"\",\"strategy\":\"sequential\",\"evaluatedInputs\":{\"TUTU\":\"TITI\"},\"stepOutputs\":{\"TUTU\":\"TITI\"}},\"contextVariables\":{}}");

    }
    @Test
    public void should_serialize_and_deserialize_complex_input_and_output_in_report() throws JsonProcessingException {
        // Given
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        Map<String, Object> mapStringObject = Map.of("TUTU", Map.of("TOTO", Map.of("BUBU",Map.of("VIVI","LALA"))));

        Step.StepContextSnapshot stepContextSnapshot = new Step.StepContextSnapshot(mapStringObject, mapStringObject, objectMapper);
        StepExecutionReportCore stepExecutionReport = new StepExecutionReportCoreBuilder()
            .setStepOutputs(new HashMap<>(stepContextSnapshot.getOutputsSnapshot()))
            .setEvaluatedInputs(new HashMap<>(stepContextSnapshot.getInputsSnapshot()))
            .createStepExecutionReport();
        ScenarioExecutionReport scenarioExecutionReport = new ScenarioExecutionReport(1L, "", "", "", List.of(), stepExecutionReport);

        // When
        String serializedReport = objectMapper.writeValueAsString(scenarioExecutionReport);

        // Then
        assertThat(serializedReport).isEqualTo("{\"executionId\":1,\"scenarioName\":\"\",\"environment\":\"\",\"user\":\"\",\"tags\":[],\"constants\":{},\"datatable\":[],\"report\":{\"name\":null,\"duration\":0,\"startDate\":null,\"status\":null,\"information\":[],\"errors\":[],\"steps\":[],\"type\":null,\"targetName\":\"\",\"targetUrl\":\"\",\"strategy\":\"sequential\",\"evaluatedInputs\":{\"TUTU\":{\"TOTO\":{\"BUBU\":{\"VIVI\":\"LALA\"}}}},\"stepOutputs\":{\"TUTU\":{\"TOTO\":{\"BUBU\":{\"VIVI\":\"LALA\"}}}}},\"contextVariables\":{}}");

    }
}
