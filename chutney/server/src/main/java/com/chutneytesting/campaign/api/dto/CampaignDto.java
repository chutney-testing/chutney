/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.api.dto;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CampaignDto {

    private Long id;
    private String title;
    private String description;
    private List<CampaignScenarioDto> scenarios;
    private List<CampaignExecutionReportDto> campaignExecutionReports;
    private String environment;
    private boolean parallelRun;
    private boolean retryAuto;
    private String datasetId;
    private List<String> tags;

    public CampaignDto() {
    }

    public CampaignDto(Long id,
                       String title,
                       String description,
                       List<CampaignScenarioDto> scenarios,
                       List<CampaignExecutionReportDto> campaignExecutionReports,
                       String environment,
                       boolean parallelRun,
                       boolean retryAuto,
                       String datasetId,
                       List<String> tags) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.scenarios = ofNullable(scenarios).orElseGet(ArrayList::new);
        this.campaignExecutionReports = ofNullable(campaignExecutionReports).orElseGet(ArrayList::new);
        this.environment = environment;
        this.parallelRun = parallelRun;
        this.retryAuto = retryAuto;
        this.datasetId = datasetId;
        this.tags = ofNullable(tags).orElseGet(ArrayList::new);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<CampaignScenarioDto> getScenarios() {
        return scenarios;
    }

    public List<CampaignExecutionReportDto> getCampaignExecutionReports() {
        return campaignExecutionReports;
    }

    public String getEnvironment() {
        return environment;
    }

    public boolean isParallelRun() {
        return parallelRun;
    }

    public boolean isRetryAuto() {
        return retryAuto;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public List<String> getTags() {
        return tags;
    }

    public record CampaignScenarioDto(String scenarioId, String datasetId) {
        public CampaignScenarioDto(String scenarioId) {
            this(scenarioId, null);
        }
    }
}
