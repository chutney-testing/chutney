/*
 *  Copyright 2017-2023 Enedis
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

import com.chutneytesting.environment.infra.eventEmitter.EnvironmentRenamingEvent;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class CampaignEventEnvironmentRenamingListener implements ApplicationListener<EnvironmentRenamingEvent> {

    private final CampaignService campaignService;
    private static final Logger LOGGER = LoggerFactory.getLogger(CampaignEventEnvironmentRenamingListener.class);

    public CampaignEventEnvironmentRenamingListener(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @Override
    public void onApplicationEvent(EnvironmentRenamingEvent environmentRenamingEvent) {
        LOGGER.debug("EnvironmentRenamingEvent received for old name {} and new name {}", environmentRenamingEvent.getOldName(), environmentRenamingEvent.getNewName());
        Objects.requireNonNull(environmentRenamingEvent);
        Objects.requireNonNull(environmentRenamingEvent.getOldName());
        Objects.requireNonNull(environmentRenamingEvent.getNewName());
        campaignService.renameEnvironmentInCampaigns(environmentRenamingEvent.getOldName(), environmentRenamingEvent.getNewName());
    }
}
