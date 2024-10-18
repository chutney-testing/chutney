/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.domain;

import com.chutneytesting.campaign.infra.CampaignScenarioJpaRepository;
import com.chutneytesting.campaign.infra.jpa.CampaignScenarioEntity;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignScenario;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class DatabaseCampaignScenarioRepository implements CampaignScenarioRepository {

    private final CampaignScenarioJpaRepository campaignScenarioJpaRepository;

    public DatabaseCampaignScenarioRepository(CampaignScenarioJpaRepository campaignScenarioJpaRepository) {
        this.campaignScenarioJpaRepository = campaignScenarioJpaRepository;
    }

    @Override
    public List<CampaignScenario> findAllByDatasetId(String datasetId) {
        return this.campaignScenarioJpaRepository.findAllByDatasetId(datasetId).stream().map(CampaignScenarioEntity::toDomain).toList();
    }
}
