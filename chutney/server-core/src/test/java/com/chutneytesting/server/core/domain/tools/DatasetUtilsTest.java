/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.tools;

import static com.chutneytesting.server.core.domain.dataset.DataSet.NO_DATASET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.chutneytesting.server.core.domain.dataset.DataSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class DatasetUtilsTest {

    @Nested
    @DisplayName("Compare datasets")
    class CompareDataset {

        @Nested
        @DisplayName("Datasets are the same when")
        class Same {
            @Test
            @DisplayName("null or NO_DATASET")
            void null_or_No_Dataset() {
                assertTrue(DatasetUtils.compareDataset(null, null));
                assertTrue(DatasetUtils.compareDataset(NO_DATASET, null));
                assertTrue(DatasetUtils.compareDataset(null, NO_DATASET));
                assertTrue(DatasetUtils.compareDataset(NO_DATASET, NO_DATASET));
            }

            @Test
            @DisplayName("ids, constants and datatables have exactly the same values")
            void should_return_true_when_comparing_datasets_with_same_values() {
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
        }

        @Nested
        @DisplayName("Datasets are not the same when")
        class NotTheSame {
            @Test
            @DisplayName("one dataset is null or NO_DATASET")
            void one_null_or_No_Dataset() {
                DataSet ds = DataSet.builder().withName("").build();
                assertFalse(DatasetUtils.compareDataset(null, ds));
                assertFalse(DatasetUtils.compareDataset(NO_DATASET, ds));
                assertFalse(DatasetUtils.compareDataset(ds, null));
                assertFalse(DatasetUtils.compareDataset(ds, NO_DATASET));
            }

            @Test
            @DisplayName("ids are not")
            void should_return_false_when_comparing_datasets_with_different_id() {
                //Given
                DataSet dataset1 = DataSet.builder().withName("").withId("DATASET_ID_1").withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE2"))).build();
                DataSet dataset2 = DataSet.builder().withName("").withId("DATASET_ID_2").withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE2"))).build();

                // When
                boolean result = DatasetUtils.compareDataset(dataset1, dataset2);

                // Then
                assertThat(result).isFalse();
            }

            @Test
            @DisplayName("constants are not")
            void should_return_false_when_comparing_datasets_with_different_constants() {
                //Given
                DataSet dataset1 = DataSet.builder().withId(null).withName("").withConstants(Map.of("HEADER", "DIFFERENT VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE2"))).build();
                DataSet dataset2 = DataSet.builder().withId(null).withName("").withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE2"))).build();

                // When
                boolean result = DatasetUtils.compareDataset(dataset1, dataset2);

                // Then
                assertThat(result).isFalse();
            }

            @Nested
            @DisplayName("datatables are not")
            class Datatable {
                @Test
                void should_return_false_when_comparing_datasets_with_different_datatable() {
                    //Given
                    DataSet dataset1 = DataSet.builder().withName("").withId(null).withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE2"))).build();
                    DataSet dataset2 = DataSet.builder().withName("").withId(null).withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE_DIFF"))).build();

                    // When
                    boolean result = DatasetUtils.compareDataset(dataset1, dataset2);

                    // Then
                    assertThat(result).isFalse();
                }

                @Test
                @DisplayName("extra row")
                void should_return_false_when_comparing_datasets_with_different_datatable_with_extra_row() {
                    //Given
                    DataSet dataset1 = DataSet.builder().withName("").withId(null).withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"))).build();
                    DataSet dataset2 = DataSet.builder().withName("").withId(null).withConstants(Map.of("HEADER", "VALUE")).withDatatable(List.of(Map.of("HEADER1", "VALUE1"), Map.of("HEADER2", "VALUE2"))).build();

                    // When
                    boolean result = DatasetUtils.compareDataset(dataset1, dataset2);

                    // Then
                    assertThat(result).isFalse();
                }
            }
        }
    }
}
