/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.domain;

import com.chutneytesting.server.core.domain.environment.UpdateEnvironmentHandler;
import com.chutneytesting.server.core.domain.scenario.campaign.Campaign;

public class CampaignEnvironmentUpdateHandler implements UpdateEnvironmentHandler {

    private final CampaignRepository campaignRepository;

    public CampaignEnvironmentUpdateHandler(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    @Override
    public void renameEnvironment(String oldName, String newName) {
        campaignRepository.findCampaignsByEnvironment(oldName)
            .forEach(existingCampaign -> {
                Campaign campaign = new Campaign(
                    existingCampaign.id,
                    existingCampaign.title,
                    existingCampaign.description,
                    existingCampaign.scenarios,
                    newName,
                    existingCampaign.parallelRun,
                    existingCampaign.retryAuto,
                    existingCampaign.executionDataset(),
                    existingCampaign.tags
                );
                campaignRepository.createOrUpdate(campaign);
            });
    }

    @Override
    public void deleteEnvironment(String environmentName) {
        campaignRepository.findCampaignsByEnvironment(environmentName)
            .forEach(existingCampaign -> {
                Campaign campaign = new Campaign(
                    existingCampaign.id,
                    existingCampaign.title,
                    existingCampaign.description,
                    existingCampaign.scenarios,
                    null,
                    existingCampaign.parallelRun,
                    existingCampaign.retryAuto,
                    existingCampaign.executionDataset(),
                    existingCampaign.tags
                );
                campaignRepository.createOrUpdate(campaign);
            });
    }
}
