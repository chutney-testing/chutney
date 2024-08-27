/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.dataset.api;


import com.chutneytesting.server.core.domain.dataset.DataSet;

public class DataSetMapper {

    public static DataSetDto toDto(DataSet dataSet) {
        return ImmutableDataSetDto.builder()
            .datasetId(dataSet.id)
            .name(dataSet.name)
            .description(dataSet.description)
            .lastUpdated(dataSet.creationDate)
            .tags(dataSet.tags)
            .constants(KeyValue.fromMap(dataSet.constants))
            .datatable(dataSet.datatable.stream().map(KeyValue::fromMap).toList())
            .build();
    }

    public static DataSet fromDto(DataSetDto dto) {
        return DataSet.builder()
            .withId(dto.datasetId().orElse(null))
            .withName(dto.name())
            .withDescription(dto.description())
            .withCreationDate(dto.lastUpdated())
            .withTags(dto.tags())
            .withConstants(KeyValue.toMap(dto.constants()))
            .withDatatable(dto.datatable().stream().map(KeyValue::toMap).toList())
            .build();
    }
}
