/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.infra;

import java.time.LocalDateTime;
import java.util.List;

public class SchedulingCampaignDto {
    public final String id;
    public final List<Long> campaignsId;
    public final List<String> campaignsTitle;
    public final LocalDateTime schedulingDate;
    public final String frequency;

    /**
     * for ObjectMapper only
     **/
    public SchedulingCampaignDto() {
        id = null;
        campaignsId = null;
        schedulingDate = null;
        campaignsTitle = null;
        frequency = null;
    }

    public SchedulingCampaignDto(String id,
                                 List<Long> campaignsId,
                                 List<String> campaignsTitle,
                                 LocalDateTime schedulingDate,
                                 String frequency) {
        this.id = id;
        this.campaignsId = campaignsId;
        this.campaignsTitle = campaignsTitle;
        this.schedulingDate = schedulingDate;
        this.frequency = frequency;
    }
}
