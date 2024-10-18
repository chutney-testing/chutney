/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.domain;

import com.chutneytesting.server.core.domain.scenario.campaign.CampaignScenario;
import java.util.List;

public interface CampaignScenarioRepository {
    List<CampaignScenario> findAllByDatasetId(String datasetId);

}
