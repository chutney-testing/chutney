/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
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
