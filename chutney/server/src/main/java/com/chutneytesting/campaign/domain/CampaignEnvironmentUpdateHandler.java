/*
 *  Copyright 2017-2024 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
                    existingCampaign.externalDatasetId,
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
                    existingCampaign.externalDatasetId,
                    existingCampaign.tags
                );
                campaignRepository.createOrUpdate(campaign);
            });
    }
}
