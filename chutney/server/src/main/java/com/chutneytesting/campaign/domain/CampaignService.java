/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.domain;

import static java.util.stream.Collectors.toList;

import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecution;
import java.util.List;

public class CampaignService {

    private final CampaignExecutionRepository campaignExecutionRepository;

    public CampaignService(CampaignExecutionRepository campaignExecutionRepository) {
        this.campaignExecutionRepository = campaignExecutionRepository;
    }

    public CampaignExecution findByExecutionId(Long campaignExecutionId) {
        CampaignExecution report = campaignExecutionRepository.getCampaignExecutionById(campaignExecutionId);
        return report.withoutRetries();
    }

    public List<CampaignExecution> findExecutionsById(Long campaignId) {
        return campaignExecutionRepository.getExecutionHistory(campaignId).stream()
            .map(CampaignExecution::withoutRetries)
            .collect(toList());
    }
}
