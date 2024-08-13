/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */


package com.chutneytesting.campaign.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SchedulingCampaignDto {

    private Long id;
    private List<Long> campaignsId;
    private List<String> campaignsTitle;
    private LocalDateTime schedulingDate;
    private String frequency;

    public SchedulingCampaignDto() {
    }

    public SchedulingCampaignDto(Long id,
                                 List<Long> campaignsId,
                                 List<String> campaignsTitle,
                                 LocalDateTime schedulingDate,
                                 String frequency
    ) {
        this.id = id;
        this.campaignsId = campaignsId;
        this.campaignsTitle = campaignsTitle;
        this.schedulingDate = schedulingDate;
        this.frequency = frequency;
    }

    public Long getId() {
        return id;
    }

    public List<Long> getCampaignsId() {
        return campaignsId;
    }

    public List<String> getCampaignsTitle() {
        return campaignsTitle;
    }

    public LocalDateTime getSchedulingDate() {
        return schedulingDate;
    }

    public String getFrequency() {
        return frequency;
    }
}
