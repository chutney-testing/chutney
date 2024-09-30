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
    private LocalDateTime schedulingDate;
    private String frequency;
    private String environment;
    private List<CampaignExecutionRequestDto> campaignExecutionRequestDto;

    public SchedulingCampaignDto() {
    }

    public SchedulingCampaignDto(Long id,
                                 LocalDateTime schedulingDate,
                                 String frequency,
                                 String environment,
                                 List<CampaignExecutionRequestDto> campaignExecutionRequestDto
    ) {
        this.id = id;
        this.schedulingDate = schedulingDate;
        this.frequency = frequency;
        this.environment = environment;
        this.campaignExecutionRequestDto = campaignExecutionRequestDto;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getSchedulingDate() {
        return schedulingDate;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getEnvironment() {
        return environment;
    }

    public List<CampaignExecutionRequestDto> getCampaignExecutionRequestDto() {
        return campaignExecutionRequestDto;
    }

    public record CampaignExecutionRequestDto(Long campaignId, String campaignTitle, String datasetId) {
    }
}
