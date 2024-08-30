/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.api.dto;

import com.chutneytesting.dataset.api.DataSetDto;
import com.chutneytesting.dataset.api.ImmutableDataSetDto;
import com.chutneytesting.dataset.api.KeyValue;
import com.chutneytesting.server.core.domain.dataset.DataSet;
import com.chutneytesting.server.core.domain.execution.history.ExecutionHistory;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecution;
import java.util.List;
import java.util.stream.Collectors;

public class CampaignExecutionReportMapper {

    public static CampaignExecutionReportDto toDto(CampaignExecution campaignReport) {
        return new CampaignExecutionReportDto(
            campaignReport.executionId,
            campaignReport.scenarioExecutionReports().stream()
                .map(ScenarioExecutionReportCampaignMapper::toDto)
                .collect(Collectors.toList()),
            campaignReport.campaignName,
            campaignReport.startDate,
            campaignReport.status(),
            campaignReport.partialExecution,
            campaignReport.executionEnvironment,
            datasetToDto(campaignReport.dataset),
            campaignReport.userId,
            campaignReport.getDuration());
    }

    public static CampaignExecutionFullReportDto fullExecutionToDto(CampaignExecution campaignReport, List<ExecutionHistory.Execution> executions) {
        return new CampaignExecutionFullReportDto(
            campaignReport.executionId,
            executions,
            campaignReport.campaignName,
            campaignReport.startDate,
            campaignReport.status(),
            campaignReport.partialExecution,
            campaignReport.executionEnvironment,
            campaignReport.userId,
            campaignReport.getDuration());
    }

    public static DataSetDto datasetToDto(DataSet dataset) {
        if (dataset == null) {
            return null;
        }
        ImmutableDataSetDto.Builder datasetBuilder = ImmutableDataSetDto.builder().name("");
        if (dataset.id != null) {
            datasetBuilder.id(dataset.id);
        }
        if (dataset.constants != null) {
            datasetBuilder.constants(KeyValue.fromMap(dataset.constants));
        }
        if (dataset.datatable != null) {
            datasetBuilder.datatable(dataset.datatable.stream().map(KeyValue::fromMap).toList());
        }
        return datasetBuilder.build();
    }
}
