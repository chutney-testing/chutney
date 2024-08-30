/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.tools;

import static org.assertj.core.api.Assertions.assertThat;

import com.chutneytesting.server.core.domain.dataset.DataSet;
import com.chutneytesting.server.core.domain.dataset.DatasetEntityMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;


public class DatasetEntityMapperTest {

    @Test
    public void should_map_external_dataset() {
        //Given
        String datasetIdString = "DATASET_ID";
        String constantsString = "{\"HEADER1\":\"VALUE1\",\"HEADER2\":\"VALUE2\"}";
        String datatableString = "[{\"HEADER1\":\"VALUE1\"},{\"HEADER2\":\"VALUE2\"}]";

        Map<String, String> constants = Map.of(
        "HEADER1", "VALUE1",
        "HEADER2", "VALUE2"
        );

        List<Map<String, String>> datatable = List.of(
            Map.of("HEADER1", "VALUE1"),
            Map.of("HEADER2", "VALUE2")
        );

        // When
        DataSet dataset = DatasetEntityMapper.getDataset(datasetIdString, constantsString, datatableString);

        // Then
        assertThat(dataset.id).isNotNull();
        assertThat(dataset.id).isEqualTo("DATASET_ID");
        assertThat(dataset.constants).isNotNull();
        assertThat(dataset.constants).containsAllEntriesOf(constants);
        assertThat(dataset.datatable).isNotNull();
        assertThat(dataset.datatable).hasSize(2);
        assertThat(dataset.datatable.get(0)).containsAllEntriesOf(datatable.get(0));
        assertThat(dataset.datatable.get(1)).containsAllEntriesOf(datatable.get(1));
    }

    @Test
    public void should_compare_same_external_dataset() {
        //Given
        DataSet dataset1 = DataSet.builder().withName("").withId("DATASET_ID").withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE2"))).build();
        DataSet dataset2 = DataSet.builder().withName("").withId("DATASET_ID").withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE2"))).build();

        // When
        boolean result = DatasetEntityMapper.compareDataset(dataset1, dataset2);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void should_compare_external_dataset_with_different_id() {
        //Given
        DataSet dataset1 = DataSet.builder().withName("").withId("DATASET_ID_1").withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE2"))).build();
        DataSet dataset2 = DataSet.builder().withName("").withId("DATASET_ID_2").withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE2"))).build();

        // When
        boolean result = DatasetEntityMapper.compareDataset(dataset1, dataset2);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_compare_external_dataset_with_different_constants() {
        //Given
        DataSet dataset1 = DataSet.builder().withId(null).withName("").withConstants(Map.of("HEADER", "DIFFERENT VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE2"))).build();
        DataSet dataset2 = DataSet.builder().withId(null).withName("").withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE2"))).build();

        // When
        boolean result = DatasetEntityMapper.compareDataset(dataset1, dataset2);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_compare_external_dataset_with_different_datatable_different_length() {
        //Given
        DataSet dataset1 = DataSet.builder().withName("").withId(null).withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"))).build();
        DataSet dataset2 = DataSet.builder().withName("").withId(null).withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE2"))).build();

        // When
        boolean result = DatasetEntityMapper.compareDataset(dataset1, dataset2);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_compare_external_dataset_with_different_datatable_different_value() {
        //Given
        DataSet dataset1 = DataSet.builder().withName("").withId(null).withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE2"))).build();
        DataSet dataset2 = DataSet.builder().withName("").withId(null).withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE_DIFF"))).build();

        // When
        boolean result = DatasetEntityMapper.compareDataset(dataset1, dataset2);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_convert_dataset_constants_to_string() {
        //Given
        Map<String, String> constants = new HashMap<>();
        constants.put("HEADER1", "VALUE1");
        constants.put("HEADER2", "VALUE2");
        constants.put("HEADER3", "VALUE3");

        // When
        String constantsString = DatasetEntityMapper.datasetConstantsToString(constants);

        // Then
        assertThat(constantsString).isEqualTo("{\"HEADER3\":\"VALUE3\",\"HEADER1\":\"VALUE1\",\"HEADER2\":\"VALUE2\"}");
    }

    @Test
    public void should_return_null_when_dataset_constants_is_empty() {
        //Given
        Map<String, String> constants = Map.of();

        // When
        String constantsString = DatasetEntityMapper.datasetConstantsToString(constants);

        // Then
        assertThat(constantsString).isNull();
    }

    @Test
    public void should_return_null_when_dataset_constants_is_null() {
        // When
        String constantsString = DatasetEntityMapper.datasetConstantsToString(null);

        // Then
        assertThat(constantsString).isNull();
    }

    @Test
    public void should_convert_dataset_datatable_to_string() {
        //Given
        List<Map<String, String>> datatable = new ArrayList<>();
        HashMap<String, String> row1 = new HashMap<>();
        row1.put("HEADER1", "VALUE1");
        row1.put("HEADER2", "VALUE2");
        HashMap<String, String> row2 = new HashMap<>();
        row2.put("HEADER1", "VALUE3");
        row2.put("HEADER2", "VALUE4");
        datatable.add(row1);
        datatable.add(row2);

        // When
        String constantsString = DatasetEntityMapper.datasetDatatableToString(datatable);

        // Then
        assertThat(constantsString).isEqualTo("[{\"HEADER1\":\"VALUE1\",\"HEADER2\":\"VALUE2\"},{\"HEADER1\":\"VALUE3\",\"HEADER2\":\"VALUE4\"}]");
    }

    @Test
    public void should_return_null_when_dataset_datatable_is_empty() {
        //Given
        List<Map<String, String>> datatable = List.of();

        // When
        String constantsString = DatasetEntityMapper.datasetDatatableToString(datatable);

        // Then
        assertThat(constantsString).isNull();
    }

    @Test
    public void should_return_null_when_dataset_datatable_is_null() {
        // When
        String constantsString = DatasetEntityMapper.datasetDatatableToString(null);

        // Then
        assertThat(constantsString).isNull();
    }
}
