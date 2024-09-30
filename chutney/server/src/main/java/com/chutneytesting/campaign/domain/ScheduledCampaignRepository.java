/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.domain;

import java.util.List;

/**
 * CRUD for SchedulingCampaign
 */
public interface ScheduledCampaignRepository {

    PeriodicScheduledCampaign add(PeriodicScheduledCampaign periodicScheduledCampaign);

    void removeById(Long id);

    void removeCampaignId(Long id);

    List<PeriodicScheduledCampaign> getAll();
}
