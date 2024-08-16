/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.execution.processor;

import static org.apache.commons.text.StringEscapeUtils.escapeJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

public class TestCasePreProcessorTest {

    @Test
    public void should_escape_when_ask_for() {
        // Given
        TestCasePreProcessor mock = mock(TestCasePreProcessor.class);
        when(mock.replaceParams(any(Map.class), any(String.class), any(Function.class))).thenCallRealMethod();

        String datasetKeyToBeReplace = "key";

        Map<String, String> dataset = new HashMap<>();
        dataset.put(datasetKeyToBeReplace, "first line \n seconde line \r third line");

        // When
        String resultMultiLine = mock.replaceParams(dataset, "to be replaced: **" + datasetKeyToBeReplace + "**", input -> escapeJson((String) input));
        assertThat(resultMultiLine).isEqualTo("to be replaced: " + escapeJson(dataset.get(datasetKeyToBeReplace)));
    }
}
