/*
 *  Copyright 2017-2023 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.chutneytesting.campaign.api.dto;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

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
