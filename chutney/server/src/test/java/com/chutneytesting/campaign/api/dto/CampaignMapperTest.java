/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.api.dto;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import com.chutneytesting.server.core.domain.scenario.ExternalDataset;
import com.chutneytesting.server.core.domain.scenario.campaign.Campaign;
import java.util.List;
import org.assertj.core.api.ThrowingConsumer;
import org.junit.jupiter.api.Test;

class CampaignMapperTest {
    @Test
    void map_scenarios_from_domain() {
        Campaign campaign = new Campaign(null, null, null,
            List.of(new Campaign.CampaignScenario("id1", "dataset1"), new Campaign.CampaignScenario("id2")),
            null, false, false, null, null
        );

        CampaignDto dtoWithoutReport = CampaignMapper.toDtoWithoutReport(campaign);
        CampaignDto dto = CampaignMapper.toDto(campaign, emptyList());

        ThrowingConsumer<CampaignDto> assertions = d -> {
            assertThat(d.getScenarios()).contains(
                new CampaignDto.CampaignScenarioDto("id1", "dataset1"),
                new CampaignDto.CampaignScenarioDto("id2")
            );
        };

        assertThat(dtoWithoutReport).satisfies(assertions);
        assertThat(dto).satisfies(assertions);
    }

    @Test
    void map_scenarios_from_dto() {
        CampaignDto dto = new CampaignDto(null, null, null,
            List.of(new CampaignDto.CampaignScenarioDto("1", "dataset1"), new CampaignDto.CampaignScenarioDto("2")),
            null, null, false, false, null, null);
        Campaign campaign = CampaignMapper.fromDto(dto);
        assertThat(campaign.scenarios).containsExactly(
            new Campaign.CampaignScenario("1", "dataset1"),
            new Campaign.CampaignScenario("2")
        );
    }
}
