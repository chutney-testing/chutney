/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.dataset.api;

import static com.chutneytesting.dataset.api.DataSetMapper.fromDto;
import static com.chutneytesting.dataset.api.DataSetMapper.toDto;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class DataSetMapperTest {

    // TODO issue https://github.com/chutney-testing/chutney-legacy/issues/532 for more details
    @Test
    @Disabled
    void constants_order_must_be_kept() {
        List<KeyValue> constants = new ArrayList<>();
        IntStream.range(1, 10).mapToObj(i -> keyOf("key" + i, "v" + i)).forEachOrdered(constants::add);

        DataSetDto dataSetDto = ImmutableDataSetDto.builder()
            .id("id")
            .name("name")
            .addAllConstants(constants)
            .build();

        DataSetDto dataset = toDto(fromDto(dataSetDto));

        assertThat(dataset.constants()).containsExactlyElementsOf(constants);
    }

    @Test
        //@Disabled
    void datatable_line_order_must_be_kept() {
        List<List<KeyValue>> datatable = List.of(
            List.of(keyOf("col1", "v11"), keyOf("col2", "v12"), keyOf("col3", "v13"), keyOf("col4", "v14")),
            List.of(keyOf("col1", "v21"), keyOf("col2", "v22"), keyOf("col3", "v23"), keyOf("col4", "v24")),
            List.of(keyOf("col1", "v31"), keyOf("col2", "v32"), keyOf("col3", "v33"), keyOf("col4", "v34")),
            List.of(keyOf("col1", "v41"), keyOf("col2", "v42"), keyOf("col3", "v43"), keyOf("col4", "v44"))
        );
        DataSetDto dataSetDto = ImmutableDataSetDto.builder()
            .id("id")
            .name("name")
            .addAllDatatable(datatable)
            .build();

        DataSetDto dataset = toDto(fromDto(dataSetDto));

        assertThat(dataset.datatable()).hasSize(4).satisfies(dt -> {
            for (int i = 0; i < 4; i++) {
                assertThat(dt.get(i)).containsExactlyInAnyOrderElementsOf(datatable.get(i));
            }
        });
    }

    @Test
    @Disabled
    void datatable_column_order_must_be_kept() {
        List<KeyValue> datatableLine = List.of(keyOf("col1", "v11"), keyOf("col2", "v12"), keyOf("col3", "v13"), keyOf("col4", "v14"));
        DataSetDto dataSetDto = ImmutableDataSetDto.builder()
            .id("id")
            .name("name")
            .addDatatable(datatableLine)
            .build();

        DataSetDto dataset = toDto(fromDto(dataSetDto));

        assertThat(dataset.datatable().get(0)).containsExactlyElementsOf(datatableLine);
    }

    private KeyValue keyOf(String key, String value) {
        return ImmutableKeyValue.builder().key(key).value(value).build();
    }
}
