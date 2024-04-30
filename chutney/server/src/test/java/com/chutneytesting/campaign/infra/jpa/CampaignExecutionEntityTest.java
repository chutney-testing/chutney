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

package com.chutneytesting.campaign.infra.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.chutneytesting.execution.infra.storage.jpa.ScenarioExecutionEntity;
import com.chutneytesting.server.core.domain.execution.report.ServerReportStatus;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecution;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class CampaignExecutionEntityTest {
    @Test
    void map_to_domain_with_multiple_datasets_scenarios_executions_while_running() {
        CampaignEntity campaign = mock(CampaignEntity.class);
        when(campaign.campaignScenarios()).thenReturn(List.of(
            new CampaignScenarioEntity(campaign, "1", "dataset1", 1),
            new CampaignScenarioEntity(campaign, "1", "dataset2", 2),
            new CampaignScenarioEntity(campaign, "2", "dataset3", 3),
            new CampaignScenarioEntity(campaign, "2", "dataset4", 4)
        ));
        List<ScenarioExecutionEntity> scenarioExecutions = List.of(
            buildScenarioExecution("1", ServerReportStatus.SUCCESS, "dataset1"),
            buildScenarioExecution("1", ServerReportStatus.RUNNING, "dataset2"),
            buildScenarioExecution("2", ServerReportStatus.NOT_EXECUTED, "dataset3"),
            buildScenarioExecution("2", ServerReportStatus.NOT_EXECUTED, "dataset4")
        );
        CampaignExecutionEntity sut = new CampaignExecutionEntity(0L, 0L, scenarioExecutions, false, "", "", "", 0);

        CampaignExecution actual = sut.toDomain(campaign, true, true, Function.identity());

        assertThat(actual.scenarioExecutionReports()).hasSameSizeAs(scenarioExecutions).satisfies(ser -> {
            assertThat(ser.get(0)).satisfies(se -> {
                assertThat(se.scenarioId()).isEqualTo("1");
                assertThat(se.execution().datasetId()).hasValue("dataset1");
            });
            assertThat(ser.get(1)).satisfies(se -> {
                assertThat(se.scenarioId()).isEqualTo("1");
                assertThat(se.execution().datasetId()).hasValue("dataset2");
            });
            assertThat(ser.get(2)).satisfies(se -> {
                assertThat(se.scenarioId()).isEqualTo("2");
                assertThat(se.execution().datasetId()).hasValue("dataset3");
            });
            assertThat(ser.get(3)).satisfies(se -> {
                assertThat(se.scenarioId()).isEqualTo("2");
                assertThat(se.execution().datasetId()).hasValue("dataset4");
            });
        });
    }

    private ScenarioExecutionEntity buildScenarioExecution(String scenarioId, ServerReportStatus status, String datasetId) {
        return new ScenarioExecutionEntity(0L, scenarioId, null, 0L, 0L, status, "", "", "", "", "", datasetId, "", 0);
    }
}
