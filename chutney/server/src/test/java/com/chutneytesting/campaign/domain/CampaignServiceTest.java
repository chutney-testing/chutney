/*
 * Copyright 2017-2023 Enedis
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

package com.chutneytesting.campaign.domain;


import static com.chutneytesting.server.core.domain.execution.report.ServerReportStatus.FAILURE;
import static com.chutneytesting.server.core.domain.execution.report.ServerReportStatus.RUNNING;
import static com.chutneytesting.server.core.domain.execution.report.ServerReportStatus.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chutneytesting.server.core.domain.execution.history.ExecutionHistory;
import com.chutneytesting.server.core.domain.execution.history.ImmutableExecutionHistory;
import com.chutneytesting.server.core.domain.scenario.campaign.Campaign;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecution;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecutionReportBuilder;
import com.chutneytesting.server.core.domain.scenario.campaign.ScenarioExecutionCampaign;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CampaignServiceTest {
    @Test
    void should_return_campaign_report_by_campaign_execution_id() {
        // G
        CampaignExecutionRepository campaignExecutionRepository = mock(CampaignExecutionRepository.class);
        CampaignRepository campaignRepository = mock(CampaignRepository.class);
        CampaignService campaignService = new CampaignService(campaignExecutionRepository, campaignRepository);

        ExecutionHistory.ExecutionSummary execution1 = ImmutableExecutionHistory.ExecutionSummary.builder()
            .executionId(1L)
            .testCaseTitle("")
            .time(LocalDateTime.now())
            .duration(0L)
            .environment("")
            .user("")
            .status(SUCCESS)
            .scenarioId("")
            .build();
        ScenarioExecutionCampaign scenarioExecutionReport1 = new ScenarioExecutionCampaign("scenario 1", "", execution1);
        ExecutionHistory.ExecutionSummary execution2 = ImmutableExecutionHistory.ExecutionSummary.builder()
            .executionId(2L)
            .testCaseTitle("")
            .time(LocalDateTime.now())
            .duration(0L)
            .environment("")
            .user("")
            .status(SUCCESS)
            .scenarioId("")
            .build();
        ScenarioExecutionCampaign scenarioExecutionReport2 = new ScenarioExecutionCampaign("scenario 2", "", execution2);
        CampaignExecution campaignReport = CampaignExecutionReportBuilder.builder()
            .setCampaignId(42L)
            .setExecutionEnvironment("test env")
            .setPartialExecution(true)
            .setCampaignName("test name")
            .setExecutionId(43L)
            .setDataSetId("dataset id test")
            .setStartDate(LocalDateTime.MAX)
            .addScenarioExecutionReport(scenarioExecutionReport1)
            .addScenarioExecutionReport(scenarioExecutionReport2)
            .build();
        when(campaignExecutionRepository.getCampaignExecutionById(anyLong())).thenReturn(campaignReport);

        // W
        CampaignExecution report = campaignService.findByExecutionId(0L);

        // T
        assertThat(report.scenarioExecutionReports()).hasSize(2);
        assertThat(report.status()).isEqualTo(SUCCESS);
        assertThat(report).usingRecursiveComparison()
            .isEqualTo(campaignReport);

    }

    @Test
    void should_keep_scenarios_executions_order_on_running_campaign_report() {
        // Given
        Long campaignId = 1L;
        CampaignExecutionRepository campaignExecutionRepository = mock(CampaignExecutionRepository.class);
        CampaignRepository campaignRepository = mock(CampaignRepository.class);
        ExecutionHistory.ExecutionSummary execution1 = ImmutableExecutionHistory.ExecutionSummary.builder()
            .executionId(1L)
            .testCaseTitle("")
            .time(LocalDateTime.now())
            .duration(0L)
            .environment("")
            .user("")
            .scenarioId("")
            .status(FAILURE)
            .build();

        ScenarioExecutionCampaign scenarioExecutionReport1 = new ScenarioExecutionCampaign("scenario 1", "", execution1);
        ExecutionHistory.ExecutionSummary execution2 = ImmutableExecutionHistory.ExecutionSummary.builder()
            .executionId(2L)
            .testCaseTitle("")
            .time(LocalDateTime.now())
            .duration(0L)
            .environment("")
            .user("")
            .scenarioId("")
            .status(SUCCESS)
            .build();

        ScenarioExecutionCampaign scenarioExecutionReport2 = new ScenarioExecutionCampaign("scenario 1", "", execution2);
        ExecutionHistory.ExecutionSummary execution3 = ImmutableExecutionHistory.ExecutionSummary.builder()
            .executionId(2L)
            .testCaseTitle("")
            .time(LocalDateTime.now())
            .duration(0L)
            .environment("")
            .user("")
            .status(RUNNING)
            .scenarioId("")
            .build();
        ScenarioExecutionCampaign scenarioExecutionReport3 = new ScenarioExecutionCampaign("scenario 2", "", execution3);
        List<CampaignExecution> allExecutions = List.of(
            CampaignExecutionReportBuilder.builder()
                // scenario exec report order is important for this test
                .addScenarioExecutionReport(scenarioExecutionReport1)
                .addScenarioExecutionReport(scenarioExecutionReport2)
                .addScenarioExecutionReport(scenarioExecutionReport3)
                .build()
        );
        when(campaignExecutionRepository.getExecutionHistory(anyLong())).thenReturn(allExecutions);
        CampaignService sut = new CampaignService(campaignExecutionRepository, campaignRepository);

        // When
        List<CampaignExecution> executionsReports = sut.findExecutionsById(campaignId);

        // Then
        assertThat(executionsReports).hasSize(1);
        assertThat(executionsReports.get(0).status()).isEqualTo(RUNNING);
        assertThat(executionsReports.get(0).scenarioExecutionReports()).hasSize(2);
        assertThat(executionsReports.get(0).scenarioExecutionReports().get(0).scenarioId()).isEqualTo("scenario 1");
        assertThat(executionsReports.get(0).scenarioExecutionReports().get(1).scenarioId()).isEqualTo("scenario 2");

    }

    @Test
    void should_return_campaign_report_by_campaign_execution_id_when_retry_scenario_executions_exist() {
        // G
        CampaignExecutionRepository campaignExecutionRepository = mock(CampaignExecutionRepository.class);
        CampaignRepository campaignRepository = mock(CampaignRepository.class);
        CampaignService campaignService = new CampaignService(campaignExecutionRepository, campaignRepository);

        String scenarioId = "scenario 1";
        ExecutionHistory.ExecutionSummary execution1 = ImmutableExecutionHistory.ExecutionSummary.builder()
            .executionId(1L)
            .testCaseTitle("")
            .time(LocalDateTime.now().minusMinutes(1))
            .duration(0L)
            .environment("")
            .user("")
            .status(FAILURE)
            .scenarioId("")
            .build();
        ScenarioExecutionCampaign scenarioExecutionReport1 = new ScenarioExecutionCampaign(scenarioId, "", execution1);
        ExecutionHistory.ExecutionSummary execution2 = ImmutableExecutionHistory.ExecutionSummary.builder()
            .executionId(2L)
            .testCaseTitle("")
            .time(LocalDateTime.now())
            .duration(0L)
            .environment("")
            .user("")
            .status(SUCCESS)
            .scenarioId("")
            .build();
        ScenarioExecutionCampaign scenarioExecutionReport2 = new ScenarioExecutionCampaign(scenarioId, "", execution2);
        CampaignExecution campaignReport = CampaignExecutionReportBuilder.builder()
            .addScenarioExecutionReport(scenarioExecutionReport1)
            .addScenarioExecutionReport(scenarioExecutionReport2)
            .build();
        when(campaignExecutionRepository.getCampaignExecutionById(anyLong())).thenReturn(campaignReport);

        // W
        CampaignExecution report = campaignService.findByExecutionId(0L);

        // T
        assertThat(report.scenarioExecutionReports()).hasSize(1);
        assertThat(report.status()).isEqualTo(SUCCESS);
    }

    @Test
    void should_return_all_executions_of_a_campaign() {
        // Given
        Long campaignId = 1L;
        CampaignExecutionRepository campaignExecutionRepository = mock(CampaignExecutionRepository.class);
        CampaignRepository campaignRepository = mock(CampaignRepository.class);
        ExecutionHistory.ExecutionSummary execution1 = ImmutableExecutionHistory.ExecutionSummary.builder()
            .executionId(1L)
            .testCaseTitle("")
            .time(LocalDateTime.now())
            .duration(0L)
            .environment("")
            .user("")
            .status(SUCCESS)
            .scenarioId("")
            .build();
        ScenarioExecutionCampaign scenarioExecutionReport1 = new ScenarioExecutionCampaign("scenario 1", "", execution1);
        ExecutionHistory.ExecutionSummary execution2 = ImmutableExecutionHistory.ExecutionSummary.builder()
            .executionId(2L)
            .testCaseTitle("")
            .time(LocalDateTime.now())
            .duration(0L)
            .environment("")
            .user("")
            .status(SUCCESS)
            .scenarioId("")
            .build();
        ScenarioExecutionCampaign scenarioExecutionReport2 = new ScenarioExecutionCampaign("scenario 2", "", execution2);
        List<CampaignExecution> allExecutions = List.of(
            CampaignExecutionReportBuilder.builder()
                .addScenarioExecutionReport(scenarioExecutionReport1)
                .build(),
            CampaignExecutionReportBuilder.builder()
                .addScenarioExecutionReport(scenarioExecutionReport2)
                .build()
        );
        when(campaignExecutionRepository.getExecutionHistory(anyLong())).thenReturn(allExecutions);
        CampaignService sut = new CampaignService(campaignExecutionRepository, campaignRepository);

        // When
        List<CampaignExecution> executionsReports = sut.findExecutionsById(campaignId);

        // Then
        assertThat(executionsReports).hasSameElementsAs(allExecutions);
    }

    @Test
    void should_return_all_executions_with_retries_of_a_campaign() {
        // Given
        Long campaignId = 1L;
        CampaignExecutionRepository campaignExecutionRepository = mock(CampaignExecutionRepository.class);
        CampaignRepository campaignRepository = mock(CampaignRepository.class);
        String scenario1Id = "scenario 1";
        ExecutionHistory.ExecutionSummary execution1 = ImmutableExecutionHistory.ExecutionSummary.builder()
            .executionId(1L)
            .testCaseTitle("")
            .time(LocalDateTime.now())
            .duration(0L)
            .environment("")
            .user("")
            .status(SUCCESS)
            .scenarioId("")
            .build();
        ScenarioExecutionCampaign scenarioExecutionReport1 = new ScenarioExecutionCampaign(scenario1Id, scenario1Id, execution1);
        ExecutionHistory.ExecutionSummary execution2 = ImmutableExecutionHistory.ExecutionSummary.builder()
            .executionId(2L)
            .testCaseTitle("")
            .time(LocalDateTime.now())
            .duration(0L)
            .environment("")
            .user("")
            .status(SUCCESS)
            .scenarioId("")
            .build();
        String scenario2Id = "scenario 2";
        ScenarioExecutionCampaign scenarioExecutionReport2 = new ScenarioExecutionCampaign(scenario2Id, "", execution2);
        ExecutionHistory.ExecutionSummary execution3 = ImmutableExecutionHistory.ExecutionSummary.builder()
            .executionId(3L)
            .testCaseTitle("")
            .time(LocalDateTime.now())
            .duration(0L)
            .environment("")
            .user("")
            .status(SUCCESS)
            .scenarioId("")
            .build();
        ScenarioExecutionCampaign scenarioExecutionReport3 = new ScenarioExecutionCampaign("scenario 3", "", execution3);
        ExecutionHistory.ExecutionSummary execution4 = ImmutableExecutionHistory.ExecutionSummary.builder()
            .executionId(4L)
            .testCaseTitle("")
            .time(LocalDateTime.now())
            .duration(0L)
            .environment("")
            .user("")
            .status(SUCCESS)
            .scenarioId("")
            .build();
        ScenarioExecutionCampaign scenarioExecutionReport4 = new ScenarioExecutionCampaign(scenario1Id, "", execution4);
        ExecutionHistory.ExecutionSummary execution5 = ImmutableExecutionHistory.ExecutionSummary.builder()
            .executionId(5L)
            .testCaseTitle("")
            .time(LocalDateTime.now())
            .duration(0L)
            .environment("")
            .user("")
            .status(SUCCESS)
            .scenarioId("")
            .build();
        ScenarioExecutionCampaign scenarioExecutionReport5 = new ScenarioExecutionCampaign(scenario2Id, "", execution5);
        List<CampaignExecution> allExecutions = List.of(
            CampaignExecutionReportBuilder.builder()
                .setExecutionId(1L)
                .addScenarioExecutionReport(scenarioExecutionReport1)
                .addScenarioExecutionReport(scenarioExecutionReport4)
                .addScenarioExecutionReport(scenarioExecutionReport3)
                .build(),
            CampaignExecutionReportBuilder.builder()
                .setExecutionId(2L)
                .addScenarioExecutionReport(scenarioExecutionReport1)
                .addScenarioExecutionReport(scenarioExecutionReport2)
                .build(),
            CampaignExecutionReportBuilder.builder()
                .setExecutionId(3L)
                .addScenarioExecutionReport(scenarioExecutionReport3)
                .addScenarioExecutionReport(scenarioExecutionReport2)
                .addScenarioExecutionReport(scenarioExecutionReport5)
                .build()
        );
        when(campaignExecutionRepository.getExecutionHistory(anyLong())).thenReturn(allExecutions);
        CampaignService sut = new CampaignService(campaignExecutionRepository, campaignRepository);

        // When
        List<CampaignExecution> executionsReports = sut.findExecutionsById(campaignId);

        // Then
        assertThat(executionsReports).hasSize(3);
        assertThat(executionsReports.get(0).scenarioExecutionReports()).hasSize(2);
        assertThat(executionsReports.get(1).scenarioExecutionReports()).hasSize(2);
        assertThat(executionsReports.get(2).scenarioExecutionReports()).hasSize(2);
    }

    @Test
    void should_rename_environment_in_campaign() {
        // Given
        CampaignExecutionRepository campaignExecutionRepository = mock(CampaignExecutionRepository.class);
        CampaignRepository campaignRepository = mock(CampaignRepository.class);
        CampaignService sut = new CampaignService(campaignExecutionRepository, campaignRepository);
        Campaign campaign1 = new Campaign(
            1L,
            "TITLE1",
            "DESCRIPTION1",
            List.of(),
            "ENV",
            false,
            false,
            "DATASET1",
            List.of()
        );
        Campaign campaign2 = new Campaign(
            2L,
            "TITLE2",
            "DESCRIPTION2",
            List.of(),
            "ENV",
            false,
            false,
            "DATASET2",
            List.of()
        );

        ArgumentCaptor<Campaign> argument = ArgumentCaptor.forClass(Campaign.class);
        when(campaignRepository.findCampaignsByEnvironment("ENV")).thenReturn(List.of(campaign1, campaign2));
        when(campaignRepository.createOrUpdate(any(Campaign.class))).thenReturn(any(Campaign.class));


        // When
        sut.renameEnvironmentInCampaigns("ENV", "NEW_ENV");

        // Then
        verify(campaignRepository, times(2)).createOrUpdate(argument.capture());
        List<Campaign> campaigns = argument.getAllValues();
        assertThat(campaigns).hasSize(2);
        assertThat("TITLE1").isEqualTo(campaigns.get(0).title);
        assertThat("NEW_ENV").isEqualTo(campaigns.get(0).executionEnvironment());
        assertThat("TITLE2").isEqualTo(campaigns.get(1).title);
        assertThat("NEW_ENV").isEqualTo(campaigns.get(1).executionEnvironment());
    }
}
