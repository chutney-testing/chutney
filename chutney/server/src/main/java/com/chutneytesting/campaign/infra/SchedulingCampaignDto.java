/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.infra;

import static java.util.Collections.emptyList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDateTime;
import java.util.List;

@JsonPropertyOrder({ "id", "schedulingDate", "environment", "campaignsId", "campaignsTitle", "datasetsId" })
public class SchedulingCampaignDto {
    public final String id;
    public final LocalDateTime schedulingDate;
    public final String frequency;
    public final String environment;

    @JsonIgnore
    public final List<CampaignExecutionRequestDto> campaignExecutionRequestDto;

    /**
     * for ObjectMapper only
     **/
    @JsonCreator
    public SchedulingCampaignDto() {
        id = null;
        schedulingDate = null;
        frequency = null;
        environment = null;
        campaignExecutionRequestDto = null;
    }

    @JsonIgnore
    public SchedulingCampaignDto(String id,
                                 LocalDateTime schedulingDate,
                                 String frequency,
                                 String environment,
                                 List<CampaignExecutionRequestDto> campaignExecutionRequestDto) {
        this.id = id;
        this.schedulingDate = schedulingDate;
        this.frequency = frequency;
        this.campaignExecutionRequestDto = campaignExecutionRequestDto;
        this.environment = environment;
    }

    public List<Long> getCampaignsId() {
        return campaignExecutionRequestDto != null ? campaignExecutionRequestDto.stream().map(cer -> cer.campaignId).toList() : emptyList();
    }

    public List<String> getCampaignsTitle() {
        return campaignExecutionRequestDto != null ? campaignExecutionRequestDto.stream().map(cer -> cer.campaignTitle).toList() : emptyList();
    }

    public List<String> getDatasetsId() {
        return campaignExecutionRequestDto != null ? campaignExecutionRequestDto.stream().map(cer -> cer.datasetId).toList() : emptyList();
    }

    public record CampaignExecutionRequestDto(Long campaignId, String campaignTitle, String datasetId) {
    }
}
