/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.domain;

import com.chutneytesting.execution.domain.campaign.CampaignExecutionEngine;
import com.chutneytesting.server.core.domain.scenario.campaign.Campaign;
import java.util.List;

/**
 * Right-side port for secondary actors of the business domain. See {@link CampaignExecutionEngine}
 *
 * Use to Store Campaign
 */
public interface CampaignRepository {

    Campaign createOrUpdate(Campaign campaign);

    boolean removeById(Long id);

    Campaign findById(Long campaignId) throws CampaignNotFoundException;

    List<Campaign> findAll();

    List<Campaign> findByName(String campaignName);

    List<String> findScenariosIds(Long campaignId);

    List<Campaign> findCampaignsByScenarioId(String scenarioId);

    List<Campaign> findCampaignsByEnvironment(String environment);

    List<Campaign> findCampaignsByDatasetId(String datasetId);
}
