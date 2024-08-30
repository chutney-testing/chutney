/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.api.dto;

import com.chutneytesting.dataset.api.DataSetDto;
import com.chutneytesting.server.core.domain.execution.report.ServerReportStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CampaignExecutionReportDto {

    private final Long executionId;
    private final String campaignName;
    private final LocalDateTime startDate;
    private final ServerReportStatus status;
    private final List<ScenarioExecutionReportOutlineDto> scenarioExecutionReports;
    private final boolean partialExecution;
    private final String executionEnvironment;
    private final DataSetDto dataset;
    @JsonProperty("user")
    private final String userId;
    private final Long duration;

    public CampaignExecutionReportDto(Long executionId,
                                      List<ScenarioExecutionReportOutlineDto> scenarioExecutionReports,
                                      String campaignName,
                                      LocalDateTime startDate,
                                      ServerReportStatus status,
                                      boolean partialExecution,
                                      String executionEnvironment,
                                      DataSetDto dataset,
                                      String userId,
                                      Long duration) {
        this.executionId = executionId;
        this.scenarioExecutionReports = scenarioExecutionReports;
        this.campaignName = campaignName;
        this.startDate = startDate;
        this.status = status;
        this.partialExecution = partialExecution;
        this.executionEnvironment = executionEnvironment;
        this.dataset = dataset;
        this.userId = userId;
        this.duration = duration;
    }

    public Long getExecutionId() {
        return executionId;
    }

    public List<ScenarioExecutionReportOutlineDto> getScenarioExecutionReports() {
        return scenarioExecutionReports;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public ServerReportStatus getStatus() {
        return status;
    }

    public Long getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "CampaignExecutionReport{" +
            "executionId=" + executionId +
            '}';
    }

    public String getCampaignName() {
        return campaignName;
    }

    public boolean isPartialExecution() {
        return partialExecution;
    }

    public String getExecutionEnvironment() {
        return executionEnvironment;
    }

    public String getUserId() {
        return userId;
    }

    public DataSetDto getDataset() {
        return dataset;
    }
}
