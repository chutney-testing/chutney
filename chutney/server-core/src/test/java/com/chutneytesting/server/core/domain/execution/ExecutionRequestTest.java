/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.execution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.chutneytesting.server.core.domain.dataset.DataSet;
import com.chutneytesting.server.core.domain.scenario.TestCase;
import com.chutneytesting.server.core.domain.scenario.TestCaseMetadata;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExecutionRequestTest {
    @Test
    void merge_tags_from_testcase_and_dataset() {
        // Given
        var testCaseTags = List.of("TAG_0", "TAG_1", "TAG_2", "TAG_3");
        var datasetTags = List.of("TAG_1", "TAG_2", "TAG_4");

        var testCase = mock(TestCase.class);
        var testCaseMetadata = mock(TestCaseMetadata.class);
        when(testCaseMetadata.tags()).thenReturn(testCaseTags);
        when(testCase.metadata()).thenReturn(testCaseMetadata);
        DataSet dataset = DataSet.builder()
            .withName("")
            .withTags(datasetTags)
            .build();

        var sut = new ExecutionRequest(testCase, "", "", dataset);

        // When / Then
        assertThat(sut.tags).containsOnly("TAG_0", "TAG_1", "TAG_2", "TAG_3", "TAG_4");
    }
}
