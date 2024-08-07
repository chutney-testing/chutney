/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.infra.jpa;

import static com.chutneytesting.tools.ExternalDatasetEntityMapper.datasetConstantsToString;
import static com.chutneytesting.tools.ExternalDatasetEntityMapper.datasetDatatableToString;

import com.chutneytesting.server.core.domain.scenario.ExternalDataset;
import com.chutneytesting.server.core.domain.scenario.campaign.Campaign;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.List;
import java.util.stream.IntStream;

@Entity(name = "CAMPAIGN_SCENARIOS")
public class CampaignScenarioEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CAMPAIGN_ID")
    private CampaignEntity campaign;

    @Column(name = "SCENARIO_ID")
    private String scenarioId;

    @Column(name = "DATASET_ID")
    private String datasetId;

    @Column(name = "DATASET_CONSTANTS")
    private String datasetConstants;

    @Column(name = "DATASET_DATATABLE")
    private String datasetDatatable;

    @Column(name = "RANK")
    private Integer rank;

    public CampaignScenarioEntity() {
    }

    private CampaignScenarioEntity(String scenarioId, ExternalDataset dataset, Integer rank) {
        this(null, scenarioId, dataset, rank);
    }

    public CampaignScenarioEntity(CampaignEntity campaign, String scenarioId, ExternalDataset dataset, Integer rank) {
        this.campaign = campaign;
        this.scenarioId = scenarioId;
        if (dataset != null) {
            this.datasetId = dataset.getDatasetId();
            this.datasetConstants = datasetConstantsToString(dataset.getConstants());
            this.datasetDatatable = datasetDatatableToString(dataset.getDatatable());
        }
        this.rank = rank;
    }

    public String scenarioId() {
        return scenarioId;
    }

    public CampaignEntity campaign() {
        return campaign;
    }

    public String datasetId() {
        return datasetId;
    }

    public String datasetConstants() {
        return datasetConstants;
    }

    public String datasetDatatable() {
        return datasetDatatable;
    }

    public void forCampaign(CampaignEntity campaign) {
        this.campaign = campaign;
    }

    public static List<CampaignScenarioEntity> fromDomain(Campaign campaign) {
        return IntStream.range(0, campaign.scenarios.size())
            .mapToObj(idx -> new CampaignScenarioEntity(campaign.scenarios.get(idx).scenarioId(), campaign.scenarios.get(idx).dataset(), idx))
            .toList();
    }
}
