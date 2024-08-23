/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.scenario.campaign;

import com.chutneytesting.server.core.domain.scenario.campaign.Campaign.CampaignScenario;
import java.util.List;

public class CampaignBuilder {
    private Long id;
    private String title;
    private String description;
    private List<CampaignScenario> campaignScenarios;
    private String environment;
    private boolean parallelRun;
    private boolean retryAuto;
    private String externalDatasetId;
    private List<String> tags;

    public static CampaignBuilder builder() {
        return new CampaignBuilder();
    }

    public CampaignBuilder(){

    }
    public CampaignBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public CampaignBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public CampaignBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public CampaignBuilder setCampaignScenarios(List<CampaignScenario> campaignScenarios) {
        this.campaignScenarios = campaignScenarios;
        return this;
    }

    public CampaignBuilder setEnvironment(String environment) {
        this.environment = environment;
        return this;
    }

    public CampaignBuilder setParallelRun(boolean parallelRun) {
        this.parallelRun = parallelRun;
        return this;
    }

    public CampaignBuilder setRetryAuto(boolean retryAuto) {
        this.retryAuto = retryAuto;
        return this;
    }

    public CampaignBuilder setExternalDatasetId(String externalDatasetId) {
        this.externalDatasetId = externalDatasetId;
        return this;
    }

    public CampaignBuilder setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public CampaignBuilder from(Campaign campaign) {
        this.id = campaign.id;
        this.title = campaign.title;
        this.description = campaign.description;
        this.campaignScenarios = campaign.scenarios;
        this.environment = campaign.executionEnvironment();
        this.parallelRun = campaign.parallelRun;
        this.retryAuto = campaign.retryAuto;
        this.externalDatasetId = campaign.executionDataset() ;
        this.tags = campaign.tags;

        return this;
    }

    public Campaign build() {
        return new Campaign(id, title, description, campaignScenarios, environment, parallelRun, retryAuto, externalDatasetId, tags);
    }
}
