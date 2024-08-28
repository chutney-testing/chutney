/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.api.dto;

import com.chutneytesting.dataset.api.ExternalDatasetDto;
import com.chutneytesting.dataset.api.ImmutableExternalDatasetDto;
import com.chutneytesting.dataset.api.KeyValue;
import com.chutneytesting.server.core.domain.execution.history.ExecutionHistory;
import com.chutneytesting.server.core.domain.scenario.ExternalDataset;
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
            externalDatasetToDto(campaignReport.dataset),
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

    public static ExternalDatasetDto externalDatasetToDto(ExternalDataset dataset) {
        if (dataset == null) {
            return null;
        }
        ImmutableExternalDatasetDto.Builder externalDatasetBuilder = ImmutableExternalDatasetDto.builder();
        if (dataset.getDatasetId() != null) {
            externalDatasetBuilder.datasetId(dataset.getDatasetId());
        }
        if (dataset.getConstants() != null) {
            externalDatasetBuilder.constants(KeyValue.fromMap(dataset.getConstants()));
        }
        if (dataset.getDatatable() != null) {
            externalDatasetBuilder.datatable(dataset.getDatatable().stream().map(KeyValue::fromMap).toList());
        }
        return externalDatasetBuilder.build();
    }
}
