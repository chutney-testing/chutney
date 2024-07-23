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

import static org.assertj.core.api.Assertions.assertThat;

import com.chutneytesting.engine.domain.execution.engine.step.jackson.ReportObjectMapperConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class StepContextSnapshotTest {

    @Test
    public void should_serialize_and_deserialize_simple_input_and_output_in_report() throws JsonProcessingException {
        // Given
        ObjectMapper objectMapper = ReportObjectMapperConfiguration.reportObjectMapper();
        Map<String, Object> mapStringObject = Map.of("inputObject", "inputValue");

        StepContextSnapshot stepContextSnapshot = new StepContextSnapshot(mapStringObject, mapStringObject);

        // When
        String serializedInput = objectMapper.writeValueAsString(stepContextSnapshot.getInputsSnapshot());
        String serializedOutput = objectMapper.writeValueAsString(stepContextSnapshot.getInputsSnapshot());

        // Then
        assertThat(serializedInput).isEqualTo("{\"inputObject\":\"inputValue\"}");
        assertThat(serializedOutput).isEqualTo("{\"inputObject\":\"inputValue\"}");

    }
    @Test
    public void should_serialize_and_deserialize_complex_input_and_output_in_report() throws JsonProcessingException {
        // Given
        ObjectMapper objectMapper = ReportObjectMapperConfiguration.reportObjectMapper();
        Map<String, Object> mapStringObject = Map.of("inputObject", Map.of("inputValue1", Map.of("inputValue2",Map.of("inputValue3","value"))));

        StepContextSnapshot stepContextSnapshot = new StepContextSnapshot(mapStringObject, mapStringObject);

        // When
        String serializedInput = objectMapper.writeValueAsString(stepContextSnapshot.getInputsSnapshot());
        String serializedOutput = objectMapper.writeValueAsString(stepContextSnapshot.getInputsSnapshot());

        // Then
        assertThat(serializedInput).isEqualTo("{\"inputObject\":{\"inputValue1\":{\"inputValue2\":{\"inputValue3\":\"value\"}}}}");
        assertThat(serializedOutput).isEqualTo("{\"inputObject\":{\"inputValue1\":{\"inputValue2\":{\"inputValue3\":\"value\"}}}}");
    }
}
