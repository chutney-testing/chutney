/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.scenario.campaign;

public class CampaignScenario {
    private Long id;
    private Campaign campaign;
    private String scenarioId;
    private String datasetId;
    private Integer rank;

    public CampaignScenario(Long id, Campaign campaign, String scenarioId, String datasetId, Integer rank) {
        this.id = id;
        this.campaign = campaign;
        this.scenarioId = scenarioId;
        this.datasetId = datasetId;
        this.rank = rank;
    }

    public Long getId() {
        return id;
    }

    public CampaignScenario setId(Long id) {
        this.id = id;
        return this;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public CampaignScenario setCampaign(Campaign campaign) {
        this.campaign = campaign;
        return this;
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public CampaignScenario setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
        return this;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public CampaignScenario setDatasetId(String datasetId) {
        this.datasetId = datasetId;
        return this;
    }

    public Integer getRank() {
        return rank;
    }

    public CampaignScenario setRank(Integer rank) {
        this.rank = rank;
        return this;
    }
}
