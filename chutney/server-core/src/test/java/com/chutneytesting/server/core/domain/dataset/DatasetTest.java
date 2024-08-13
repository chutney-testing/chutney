/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.dataset;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class DatasetTest {

    @Test
    void name_is_stripped() {
        String expectedName = "name with spaces";
        DataSet actual = DataSet.builder().withName("   name   with   spaces  ").build();
        assertThat(actual.name).isEqualTo(expectedName);
    }

    @Test
    void name_is_mandatory() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> DataSet.builder().build())
            .withMessage("Dataset name mandatory");
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "  "})
    void id_must_not_be_blank(String id) {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> DataSet.builder().withId(id).build())
            .withMessage("Dataset id cannot be blank");
    }

    @Test
    void empty_keys_and_lines_are_ignored() {
        // Edge case
        DataSet dataSet = DataSet.builder()
            .withName("my name")
            .withConstants(
                Map.of("", "")
            )
            .withDatatable(asList(
                Map.of("", ""),
                Map.of("", "value")
            ))
            .build();

        assertThat(dataSet.constants).isEmpty();
        assertThat(dataSet.datatable).isEmpty();

        // Normal case
        Map<String, String> expectedMap = Map.of("key1", "value", "key2", "value");
        dataSet = DataSet.builder()
            .withName("my name")
            .withConstants(
                Map.of("key1", "value", "", "value", "key2", "value")
            )
            .withDatatable(asList(
                Map.of("key1", "value", "", "", "key2", "value"),
                Map.of("key1", "", "", "", "key2", ""),
                Map.of("key1", "value", "", "value", "key2", "value")
            ))
            .build();

        assertThat(dataSet.constants).containsExactlyInAnyOrderEntriesOf(expectedMap);
        assertThat(dataSet.datatable).containsExactly(expectedMap, expectedMap);
    }

    @Test
    void keys_and_values_are_trimmed() {
        Map<String, String> expectedMap = Map.of("key1", "value", "key2", "value");
        DataSet dataSet = DataSet.builder()
            .withName("my name")
            .withConstants(
                Map.of("key1 ", "value ", "", "value", " key2   ", "value")
            )
            .withDatatable(asList(
                Map.of("key1", " value", "", "", "key2     ", "value"),
                Map.of("key1", "", "", "", "key2  ", ""),
                Map.of("key1 ", "value", "", " value", "key2", "value")
            ))
            .build();

        assertThat(dataSet.constants).containsExactlyInAnyOrderEntriesOf(expectedMap);
        assertThat(dataSet.datatable).containsExactly(expectedMap, expectedMap);
    }
}
