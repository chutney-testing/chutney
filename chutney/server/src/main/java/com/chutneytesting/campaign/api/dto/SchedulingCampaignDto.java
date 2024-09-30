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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SchedulingCampaignDto {

    private Long id;
    private LocalDateTime schedulingDate;
    private String frequency;
    private String environment;

    private List<Long> campaignsId;
    private List<String> campaignsTitle;
    private List<String> datasetsId;

    @JsonCreator
    public SchedulingCampaignDto() {
    }

    public SchedulingCampaignDto(Long id,
                                 LocalDateTime schedulingDate,
                                 String frequency,
                                 String environment,
                                 List<Long> campaignsId,
                                 List<String> campaignsTitle,
                                 List<String> datasetsId
                                 ) {
        this.id = id;
        this.schedulingDate = schedulingDate;
        this.frequency = frequency;
        this.environment = environment;
        this.campaignsId = campaignsId;
        this.campaignsTitle = campaignsTitle;
        this.datasetsId = datasetsId;
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

    public List<Long> getCampaignsId() {
        return this.campaignsId;
    }

    public List<String> getCampaignsTitle() {
        return this.campaignsTitle;
    }

    public List<String> getDatasetsId() {
        return this.datasetsId;
    }

    public static SchedulingCampaignDto toDto(PeriodicScheduledCampaign sc) {
        return new SchedulingCampaignDto(sc.id,
            sc.nextExecutionDate,
            sc.frequency.label,
            sc.environment,
            sc.campaignExecutionRequests.stream().map(CampaignExecutionRequest::campaignId).toList(),
            sc.campaignExecutionRequests.stream().map(CampaignExecutionRequest::campaignTitle).toList(),
            sc.campaignExecutionRequests.stream().map(CampaignExecutionRequest::datasetId).toList());
    }

    public static PeriodicScheduledCampaign fromDto(SchedulingCampaignDto dto) {

        List<CampaignExecutionRequest> campaignExecutionRequests = IntStream.range(0, dto.campaignsId.size())
            .mapToObj(i -> new CampaignExecutionRequest(
                dto.campaignsId.get(i),
                dto.campaignsTitle.get(i),
                guardArrayOfBoundException(dto.datasetsId, i))
            )
            .toList();

        return new PeriodicScheduledCampaign(dto.id, dto.getSchedulingDate(), toFrequency(dto.getFrequency()), dto.getEnvironment(), campaignExecutionRequests);
    }

    private static String guardArrayOfBoundException(List<String> list, int i) {
        if (i < list.size()) {
            return list.get(i);
        }
        return "";
    }
}
