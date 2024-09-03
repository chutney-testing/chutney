/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.tools;

import static org.assertj.core.api.Assertions.assertThat;

import com.chutneytesting.server.core.domain.dataset.DataSet;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class DatasetUtilsTest {

    @Test
    public void should_compare_same_external_dataset() {
        //Given
        DataSet dataset1 = DataSet.builder().withName("").withId("DATASET_ID").withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE2"))).build();
        DataSet dataset2 = DataSet.builder().withName("").withId("DATASET_ID").withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE2"))).build();

        // When
        boolean result = DatasetUtils.compareDataset(dataset1, dataset2);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void should_compare_external_dataset_with_different_id() {
        //Given
        DataSet dataset1 = DataSet.builder().withName("").withId("DATASET_ID_1").withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE2"))).build();
        DataSet dataset2 = DataSet.builder().withName("").withId("DATASET_ID_2").withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE2"))).build();

        // When
        boolean result = DatasetUtils.compareDataset(dataset1, dataset2);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_compare_external_dataset_with_different_constants() {
        //Given
        DataSet dataset1 = DataSet.builder().withId(null).withName("").withConstants(Map.of("HEADER", "DIFFERENT VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE2"))).build();
        DataSet dataset2 = DataSet.builder().withId(null).withName("").withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE2"))).build();

        // When
        boolean result = DatasetUtils.compareDataset(dataset1, dataset2);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_compare_external_dataset_with_different_datatable_different_length() {
        //Given
        DataSet dataset1 = DataSet.builder().withName("").withId(null).withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"))).build();
        DataSet dataset2 = DataSet.builder().withName("").withId(null).withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE2"))).build();

        // When
        boolean result = DatasetUtils.compareDataset(dataset1, dataset2);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_compare_external_dataset_with_different_datatable_different_value() {
        //Given
        DataSet dataset1 = DataSet.builder().withName("").withId(null).withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE2"))).build();
        DataSet dataset2 = DataSet.builder().withName("").withId(null).withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE_DIFF"))).build();

        // When
        boolean result = DatasetUtils.compareDataset(dataset1, dataset2);

        // Then
        assertThat(result).isFalse();
    }
}
