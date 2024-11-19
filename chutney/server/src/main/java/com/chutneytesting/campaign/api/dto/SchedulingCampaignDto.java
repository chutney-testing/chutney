/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */


package com.chutneytesting.campaign.api.dto;

import static com.chutneytesting.campaign.domain.Frequency.toFrequency;

import com.chutneytesting.campaign.domain.PeriodicScheduledCampaign;
import com.chutneytesting.campaign.domain.PeriodicScheduledCampaign.CampaignExecutionRequest;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SchedulingCampaignDto {

    private Long id;
    private LocalDateTime schedulingDate;
    private String frequency;
    private String environment;

    private List<CampaignExecutionRequestDto> campaignExecutionRequest = new ArrayList<>();

    @JsonCreator
    public SchedulingCampaignDto() {
    }

    public SchedulingCampaignDto(Long id,
                                 LocalDateTime schedulingDate,
                                 String frequency,
                                 String environment,
                                 List<CampaignExecutionRequestDto> campaignExecutionRequest
    ) {
        this.id = id;
        this.schedulingDate = schedulingDate;
        this.frequency = frequency;
        this.environment = environment;
        this.campaignExecutionRequest = campaignExecutionRequest;
    }

    public record CampaignExecutionRequestDto(Long campaignId, String campaignTitle, String datasetId) {
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

    public List<CampaignExecutionRequestDto> getCampaignExecutionRequest() {
        return campaignExecutionRequest;
    }

    public static SchedulingCampaignDto toDto(PeriodicScheduledCampaign sc) {
        return new SchedulingCampaignDto(sc.id,
            sc.nextExecutionDate,
            sc.frequency.label,
            sc.environment,
            sc.campaignExecutionRequests.stream().map(cer -> new CampaignExecutionRequestDto(cer.campaignId(), cer.campaignTitle(), cer.datasetId())).toList()
        );
    }

    public static PeriodicScheduledCampaign fromDto(SchedulingCampaignDto dto) {
        return new PeriodicScheduledCampaign(
            dto.id,
            dto.getSchedulingDate(),
            toFrequency(dto.getFrequency()),
            dto.getEnvironment(),
            dto.campaignExecutionRequest.stream().map(cer -> new CampaignExecutionRequest(cer.campaignId(), cer.campaignTitle(), cer.datasetId())).toList()
        );
    }
}
