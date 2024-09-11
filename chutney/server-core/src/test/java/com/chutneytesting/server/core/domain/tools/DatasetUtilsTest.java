/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.tools;

import static org.assertj.core.api.Assertions.assertThat;

import com.chutneytesting.server.core.domain.dataset.DataSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class DatasetUtilsTest {

    @Test
    public void should_compare_same_external_dataset() {
        //Given
        Map<String, String> constants1 = new HashMap<>();
        constants1.put("HEADER", "VALUE");
        Map<String, String> constants2 = new HashMap<>();
        constants1.put("HEADER", "VALUE");

        List<Map<String, String>> datatable1 = new ArrayList<>();
        HashMap<String, String> map1Datatable1 = new HashMap<>();
        HashMap<String, String> map2Datatable1 = new HashMap<>();
        map1Datatable1.put("HEADER1", "VALUE1");
        map2Datatable1.put("HEADER2", "VALUE2");
        datatable1.add(map1Datatable1);
        datatable1.add(map2Datatable1);

        List<Map<String, String>> datatable2 = new ArrayList<>();
        HashMap<String, String> map1Datatable2 = new HashMap<>();
        HashMap<String, String> map2Datatable2 = new HashMap<>();
        map1Datatable2.put("HEADER1", "VALUE1");
        map2Datatable2.put("HEADER2", "VALUE2");
        datatable2.add(map1Datatable2);
        datatable2.add(map2Datatable2);

        DataSet dataset1 = DataSet.builder().withName("").withId("DATASET_ID").withConstants(constants1).withDatatable(datatable1).build();
        DataSet dataset2 = DataSet.builder().withName("").withId("DATASET_ID").withConstants(constants2).withDatatable(datatable2).build();

        // When
        boolean result = DatasetUtils.compareDataset(dataset1, dataset2);

        // Then
        assertThat(dataset2.constants.equals(dataset1.constants) &&
            dataset2.datatable.equals(dataset1.datatable)).isFalse();
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
