/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.api.dto;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

import com.chutneytesting.server.core.domain.scenario.ExternalDataset;
import com.chutneytesting.server.core.domain.scenario.campaign.Campaign;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecution;
import java.util.List;

public class CampaignMapper {

    public static CampaignDto toDtoWithoutReport(Campaign campaign) {
        return new CampaignDto(
            campaign.id,
            campaign.title,
            campaign.description,
            campaign.scenarios.stream().map(CampaignMapper::toDto).toList(),
            emptyList(),
            campaign.executionEnvironment(),
            campaign.parallelRun,
            campaign.retryAuto,
            campaign.externalDatasetId,
            campaign.tags);
    }

    public static CampaignDto toDto(Campaign campaign, List<CampaignExecution> campaignExecutions) {
        return new CampaignDto(
            campaign.id,
            campaign.title,
            campaign.description,
            campaign.scenarios.stream().map(CampaignMapper::toDto).toList(),
            reportToDto(campaignExecutions),
            campaign.executionEnvironment(),
            campaign.parallelRun,
            campaign.retryAuto,
            campaign.externalDatasetId,
            campaign.tags);
    }

    public static Campaign fromDto(CampaignDto dto) {
        return new Campaign(
            dto.getId(),
            dto.getTitle(),
            dto.getDescription(),
            campaignScenariosFromDto(dto),
            dto.getEnvironment(),
            dto.isParallelRun(),
            dto.isRetryAuto(),
            dto.getDatasetId(),
            dto.getTags().stream().map(String::trim).map(String::toUpperCase).collect(toList())
        );
    }

    public static CampaignDto.CampaignScenarioDto toDto(Campaign.CampaignScenario campaignScenario) {
        return new CampaignDto.CampaignScenarioDto(campaignScenario.scenarioId(), campaignScenario.datasetId());
    }

    public static Campaign.CampaignScenario fromDto(CampaignDto.CampaignScenarioDto dto) {
        return new Campaign.CampaignScenario(dto.scenarioId(), dto.datasetId());
    }

    private static List<CampaignExecutionReportDto> reportToDto(List<CampaignExecution> campaignExecutions) {
        return campaignExecutions != null ? campaignExecutions.stream()
            .map(CampaignExecutionReportMapper::toDto)
            .collect(toList()) : emptyList();
    }

    private static List<Campaign.CampaignScenario> campaignScenariosFromDto(CampaignDto dto) {
        return ofNullable(dto.getScenarios()).filter(not(List::isEmpty))
            .map(list -> list.stream().map(sc -> new Campaign.CampaignScenario(sc.scenarioId(), sc.datasetId())).toList())
            .orElse(emptyList());
    }

    private static ExternalDatasetDto externalDatasetToDto(ExternalDataset externalDataset) {
        return new ExternalDatasetDto(externalDataset.getDatasetId(), externalDataset.getConstants(), externalDataset.getDatatable());
    }

    private static ExternalDataset externalDatasetFromDto(ExternalDatasetDto externalDataset) {
        return new ExternalDataset(externalDataset.datasetId(), externalDataset.constants(), externalDataset.datatable());
    }
}
