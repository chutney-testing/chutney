/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.dataset.api;


import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import com.chutneytesting.server.core.domain.dataset.DataSet;
import com.chutneytesting.dataset.domain.DataSetUsage;
import org.springframework.util.CollectionUtils;

public class DataSetMapper {

    public static DataSetUsageDto toDataSetUsageDto(DataSetUsage dataSetUsage) {
        ImmutableDataSetUsageDto.Builder datasetBuilder = ImmutableDataSetUsageDto.builder();
        if (dataSetUsage.dataSet != null) datasetBuilder.dataset(toDto(dataSetUsage.dataSet));
        if (dataSetUsage.campaignUsage != null) datasetBuilder.campaignUsage(dataSetUsage.campaignUsage);
        if (dataSetUsage.scenarioUsage != null) datasetBuilder.scenarioUsage(dataSetUsage.scenarioUsage);
        if (dataSetUsage.scenarioInCampaignUsage != null) datasetBuilder.scenarioInCampaignUsage(dataSetUsage.scenarioInCampaignUsage);
        return datasetBuilder.build();
    }

    public static DataSetDto toDto(DataSet dataSet) {
        if (dataSet == null) {
            return null;
        }
        ImmutableDataSetDto.Builder datasetBuilder = ImmutableDataSetDto.builder().name("");
        if (dataSet.id != null) datasetBuilder.id(dataSet.id);
        if (dataSet.name != null) datasetBuilder.name(dataSet.name);
        if (dataSet.constants != null) datasetBuilder.constants(KeyValue.fromMap(dataSet.constants));
        if (dataSet.datatable != null) datasetBuilder.datatable(dataSet.datatable.stream().map(KeyValue::fromMap).toList());
        if (dataSet.tags != null) datasetBuilder.tags(dataSet.tags);
        if (dataSet.description != null) datasetBuilder.description(dataSet.description);
        if (dataSet.creationDate != null) datasetBuilder.lastUpdated(dataSet.creationDate);
        return datasetBuilder.build();
    }

    public static DataSet fromDto(DataSetDto dto) {
        return DataSet.builder()
            .withId(dto.id().orElse(null))
            .withName(dto.name())
            .withDescription(dto.description())
            .withCreationDate(dto.lastUpdated())
            .withTags(dto.tags())
            .withConstants(KeyValue.toMap(dto.constants()))
            .withDatatable(ofNullable(dto.datatable()).map(datatable -> datatable.stream().map(KeyValue::toMap).toList()).orElse(emptyList()))
            .build();
    }

    public static DataSet fromExecutionDatasetDto(ExecutionDatasetDto dto) {
        if (dto == null) {
            return null;
        }
        else if (dto.getId() == null &&
            CollectionUtils.isEmpty(dto.getConstants()) &&
            CollectionUtils.isEmpty(dto.getDatatable())) {
            return null;
        }
        return DataSet.builder()
            .withId(dto.getId())
            .withName("")
            .withConstants(ofNullable(dto.getConstants()).map(KeyValue::toMap).orElse(null))
            .withDatatable(ofNullable(dto.getDatatable()).map(datatable -> datatable.stream().map(KeyValue::toMap).toList()).orElse(null))
            .build();
    }
}
