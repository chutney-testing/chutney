/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.scenario.campaign;

import com.chutneytesting.server.core.domain.execution.report.ServerReportStatus;
import com.chutneytesting.server.core.domain.scenario.ExternalDataset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CampaignExecutionReportBuilder {
    // Mandatory fields
    private Long executionId;
    private String campaignName;
    private boolean partialExecution;
    private String executionEnvironment;
    private ExternalDataset externalDataset;
    private String userId;

    // Optional fields
    private List<ScenarioExecutionCampaign> scenarioExecutionReports = new ArrayList<>();
    private Long campaignId;
    private LocalDateTime startDate;
    private ServerReportStatus status;

    public static CampaignExecutionReportBuilder builder() {
        return new CampaignExecutionReportBuilder();
    }

    private CampaignExecutionReportBuilder() {

    }

    public CampaignExecutionReportBuilder executionId(Long executionId) {
        this.executionId = executionId;
        return this;
    }

    public CampaignExecutionReportBuilder campaignName(String campaignName) {
        this.campaignName = campaignName;
        return this;
    }

    public CampaignExecutionReportBuilder partialExecution(boolean partialExecution) {
        this.partialExecution = partialExecution;
        return this;
    }

    public CampaignExecutionReportBuilder environment(String executionEnvironment) {
        this.executionEnvironment = executionEnvironment;
        return this;
    }

    public CampaignExecutionReportBuilder startDate(LocalDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public CampaignExecutionReportBuilder status(ServerReportStatus status) {
        this.status = status;
        return this;
    }

    public CampaignExecutionReportBuilder externalDataset(ExternalDataset externalDataset) {
        this.externalDataset = externalDataset;
        return this;
    }

    public CampaignExecutionReportBuilder userId(String userId) {
        this.userId = userId;
        return this;
    }

    public CampaignExecutionReportBuilder addScenarioExecutionReport(ScenarioExecutionCampaign scenarioExecutionReport) {
        this.scenarioExecutionReports.add(scenarioExecutionReport);
        return this;
    }

    public CampaignExecutionReportBuilder scenarioExecutionReport(List<ScenarioExecutionCampaign> scenarioExecutionsReports) {
        this.scenarioExecutionReports = new ArrayList<>(scenarioExecutionsReports);
        return this;
    }

    public CampaignExecutionReportBuilder campaignId(Long campaignId) {
        this.campaignId = campaignId;
        return this;
    }

    public CampaignExecution build() {
        return new CampaignExecution(
            executionId,
            campaignId,
            campaignName,
            partialExecution,
            executionEnvironment,
            userId,
            externalDataset,
            startDate,
            status,
            scenarioExecutionReports
        );
    }
}
