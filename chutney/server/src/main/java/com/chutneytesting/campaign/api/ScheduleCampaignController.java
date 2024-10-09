/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.api;

import static com.chutneytesting.campaign.domain.Frequency.toFrequency;

import com.chutneytesting.campaign.api.dto.SchedulingCampaignDto;
import com.chutneytesting.campaign.api.dto.SchedulingCampaignDto.CampaignExecutionRequestDto;
import com.chutneytesting.campaign.domain.PeriodicScheduledCampaign;
import com.chutneytesting.campaign.domain.PeriodicScheduledCampaign.CampaignExecutionRequest;
import com.chutneytesting.campaign.domain.ScheduledCampaignRepository;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ui/campaign/v1/scheduling")
@CrossOrigin(origins = "*")
public class ScheduleCampaignController {

    private final ScheduledCampaignRepository scheduledCampaignRepository;

    public ScheduleCampaignController(ScheduledCampaignRepository scheduledCampaignRepository) {
        this.scheduledCampaignRepository = scheduledCampaignRepository;
    }

    @PreAuthorize("hasAuthority('CAMPAIGN_READ')")
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SchedulingCampaignDto> getAll() {
        return scheduledCampaignRepository.getAll().stream()
            .map(sc -> new SchedulingCampaignDto(sc.id, sc.nextExecutionDate, sc.frequency.label, sc.environment, sc.campaignExecutionRequests.stream().map(aa -> new CampaignExecutionRequestDto(aa.campaignId(), aa.campaignTitle(), aa.datasetId())).toList()))
            .sorted(Comparator.comparing(SchedulingCampaignDto::getSchedulingDate))
            .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('CAMPAIGN_WRITE')")
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void add(@RequestBody SchedulingCampaignDto dto) {
        scheduledCampaignRepository.add(new PeriodicScheduledCampaign(null, dto.getSchedulingDate(), toFrequency(dto.getFrequency()), dto.getEnvironment(), dto.getCampaignExecutionRequestDto().stream().map(aa -> new CampaignExecutionRequest(aa.campaignId(), aa.campaignTitle(), aa.datasetId())).toList()));
    }

    @PreAuthorize("hasAuthority('CAMPAIGN_WRITE')")
    @DeleteMapping(path = "/{schedulingCampaignId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void delete(@PathVariable("schedulingCampaignId") Long schedulingCampaignId) {
        scheduledCampaignRepository.removeById(schedulingCampaignId);
    }

}
