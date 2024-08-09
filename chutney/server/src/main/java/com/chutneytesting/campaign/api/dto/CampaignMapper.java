/*
 * Copyright 2017-2024 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chutneytesting.campaign.api.dto;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

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
}
