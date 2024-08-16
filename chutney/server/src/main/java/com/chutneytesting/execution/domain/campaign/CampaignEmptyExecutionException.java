/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.execution.domain.campaign;

import com.chutneytesting.server.core.domain.scenario.campaign.Campaign;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecution;

public class CampaignEmptyExecutionException extends RuntimeException {
    public CampaignEmptyExecutionException(Campaign campaign) {
        super(String.format("Campaign [%s] has no associated scenarios to execute", campaign.title));
    }

    public CampaignEmptyExecutionException(CampaignExecution campaignExecution) {
        super(String.format("Campaign [%s] has no failed scenarios' executions to re-execute for execution [%d]", campaignExecution.campaignName, campaignExecution.executionId));
    }
}
