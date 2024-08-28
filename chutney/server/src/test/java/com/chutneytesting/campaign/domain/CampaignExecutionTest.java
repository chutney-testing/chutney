/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.domain;

import static com.chutneytesting.server.core.domain.execution.report.ServerReportStatus.FAILURE;
import static com.chutneytesting.server.core.domain.execution.report.ServerReportStatus.NOT_EXECUTED;
import static com.chutneytesting.server.core.domain.execution.report.ServerReportStatus.RUNNING;
import static com.chutneytesting.server.core.domain.execution.report.ServerReportStatus.STOPPED;
import static com.chutneytesting.server.core.domain.execution.report.ServerReportStatus.SUCCESS;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.chutneytesting.scenario.domain.gwt.GwtTestCase;
import com.chutneytesting.server.core.domain.execution.history.ExecutionHistory;
import com.chutneytesting.server.core.domain.execution.history.ImmutableExecutionHistory;
import com.chutneytesting.server.core.domain.execution.report.ServerReportStatus;
import com.chutneytesting.server.core.domain.scenario.ExternalDataset;
import com.chutneytesting.server.core.domain.scenario.TestCase;
import com.chutneytesting.server.core.domain.scenario.TestCaseMetadataImpl;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecution;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecutionReportBuilder;
import com.chutneytesting.server.core.domain.scenario.campaign.ScenarioExecutionCampaign;
import com.chutneytesting.server.core.domain.scenario.campaign.TestCaseDataset;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CampaignExecutionTest {

    @Nested
    @DisplayName("Execution construction")
    class Instantiation {

        @Test
        void take_the_earliest_scenario_start_date_as_start_date() {
            // Given
            ScenarioExecutionCampaign execution_noTime = new ScenarioExecutionCampaign("1", "...", mock(ExecutionHistory.ExecutionSummary.class));

            ExecutionHistory.ExecutionSummary execution_5mn = mock(ExecutionHistory.ExecutionSummary.class);
            when(execution_5mn.time()).thenReturn(LocalDateTime.now().minusMinutes(5));
            ScenarioExecutionCampaign scenarioReport_5mn = new ScenarioExecutionCampaign("2", "...", execution_5mn);

            ExecutionHistory.ExecutionSummary execution_2mn = mock(ExecutionHistory.ExecutionSummary.class);
            when(execution_2mn.time()).thenReturn(LocalDateTime.now().minusMinutes(2));
            ScenarioExecutionCampaign scenarioReport_2mn = new ScenarioExecutionCampaign("3", "...", execution_2mn);

            // When
            CampaignExecution campaignReport = CampaignExecutionReportBuilder.builder()
                .addScenarioExecutionReport(execution_noTime)
                .addScenarioExecutionReport(scenarioReport_5mn)
                .addScenarioExecutionReport(scenarioReport_2mn)
                .build();

            // Then
            assertThat(campaignReport.startDate).isEqualTo(execution_5mn.time());
        }

        @Test
        void set_start_date_as_min_possible_date_when_no_scenario_times_are_available() {
            // Given
            ScenarioExecutionCampaign scenarioReport1 = new ScenarioExecutionCampaign("1", "...", mock(ExecutionHistory.ExecutionSummary.class));
            ScenarioExecutionCampaign scenarioReport2 = new ScenarioExecutionCampaign("2", "...", mock(ExecutionHistory.ExecutionSummary.class));

            // When
            CampaignExecution campaignReport = CampaignExecutionReportBuilder.builder()
                .addScenarioExecutionReport(scenarioReport1)
                .addScenarioExecutionReport(scenarioReport2)
                .build();

            // Then
            assertThat(campaignReport.startDate).isEqualTo(LocalDateTime.MIN);
        }

        @Test
        void start_campaign_execution_when_no_scenario_reports() {
            // When
            LocalDateTime beforeInstanciation = LocalDateTime.now().minusSeconds(1);
            CampaignExecution campaignReport = CampaignExecutionReportBuilder.builder().build();
            // Then
            assertThat(campaignReport.startDate).isAfter(beforeInstanciation);
        }

        @Test
        void set_status_when_empty_scenario_reports() {
            // When
            CampaignExecution campaignReport = CampaignExecutionReportBuilder.builder().build();
            // Then
            assertThat(campaignReport.status()).isEqualTo(SUCCESS);
        }

        @Test
        void set_to_worst_status_when_scenario_reports() {
            // Given
            ScenarioExecutionCampaign execution_noStatus = new ScenarioExecutionCampaign("1", "...", mock(ExecutionHistory.ExecutionSummary.class));

            ExecutionHistory.ExecutionSummary execution_SUCESS = mock(ExecutionHistory.ExecutionSummary.class);
            when(execution_SUCESS.status()).thenReturn(SUCCESS);
            ScenarioExecutionCampaign scenarioReport_SUCCESS = new ScenarioExecutionCampaign("2", "...", execution_SUCESS);

            ExecutionHistory.ExecutionSummary execution_FAILURE = mock(ExecutionHistory.ExecutionSummary.class);
            when(execution_FAILURE.status()).thenReturn(FAILURE);
            ScenarioExecutionCampaign scenarioReport_FAILURE = new ScenarioExecutionCampaign("3", "...", execution_FAILURE);
            // When
            CampaignExecution campaignReport = CampaignExecutionReportBuilder.builder()
                .addScenarioExecutionReport(execution_noStatus)
                .addScenarioExecutionReport(scenarioReport_SUCCESS)
                .addScenarioExecutionReport(scenarioReport_FAILURE)
                .build();
            // Then
            assertThat(campaignReport.status()).isEqualTo(FAILURE);
        }
    }

    @Nested
    @DisplayName("Execution workflow")
    class ExecutionWorkflow {

        @Test
        void start_scenario_execution() {
            // Given
            CampaignExecution campaignReport = CampaignExecutionReportBuilder.builder()
                .dataset(new ExternalDataset("ds551"))
                .userId("user")
                .build();
            TestCase testCase = buildTestCase("1", "title");
            var testcaseToExecute = new TestCaseDataset(testCase, null);
            LocalDateTime beforeStartExecution = LocalDateTime.now().minusSeconds(1);

            // When
            campaignReport.addScenarioExecution(singletonList(testcaseToExecute), "env");

            // Then
            assertThat(campaignReport.scenarioExecutionReports()).hasSize(1);
            assertThat(campaignReport.scenarioExecutionReports().get(0)).satisfies(report -> {
                assertThat(report.scenarioId()).isEqualTo(testCase.metadata().id());
                assertThat(report.scenarioName()).isEqualTo(testCase.metadata().title());
                assertThat(report.execution()).satisfies(executionSummary -> {
                    assertThat(executionSummary.executionId()).isEqualTo(-1L);
                    assertThat(executionSummary.time()).isAfter(beforeStartExecution);
                    assertThat(executionSummary.status()).isEqualTo(NOT_EXECUTED);
                    assertThat(executionSummary.environment()).isEqualTo("env");
                    assertThat(executionSummary.dataset()).hasValue(campaignReport.dataset);
                    assertThat(executionSummary.user()).isEqualTo(campaignReport.userId);
                });
            });

            // When
            campaignReport.startScenarioExecution(testcaseToExecute, "env");

            // Then
            assertThat(campaignReport.scenarioExecutionReports()).hasSize(1);
            assertThat(campaignReport.scenarioExecutionReports().get(0)).satisfies(report -> {
                assertThat(report.scenarioId()).isEqualTo(testCase.metadata().id());
                assertThat(report.scenarioName()).isEqualTo(testCase.metadata().title());
                assertThat(report.execution()).satisfies(executionSummary -> {
                    assertThat(executionSummary.executionId()).isEqualTo(-1L);
                    assertThat(executionSummary.time()).isAfter(beforeStartExecution);
                    assertThat(executionSummary.status()).isEqualTo(RUNNING);
                    assertThat(executionSummary.environment()).isEqualTo("env");
                    assertThat(executionSummary.user()).isEqualTo(campaignReport.userId);
                    assertThat(executionSummary.dataset()).hasValue(campaignReport.dataset);
                });
            });

            // When
            ExecutionHistory.Execution scenarioExecution = mock(ExecutionHistory.Execution.class);
            when(scenarioExecution.scenarioId()).thenReturn(testCase.metadata().id());
            when(scenarioExecution.testCaseTitle()).thenReturn(testCase.metadata().title());
            when(scenarioExecution.executionId()).thenReturn(666L);
            when(scenarioExecution.dataset()).thenReturn(Optional.of(campaignReport.dataset));

            campaignReport.updateScenarioExecutionId(scenarioExecution);

            // Then
            assertThat(campaignReport.scenarioExecutionReports()).hasSize(1);
            assertThat(campaignReport.scenarioExecutionReports().get(0)).satisfies(report -> {
                assertThat(report.scenarioId()).isEqualTo(testCase.metadata().id());
                assertThat(report.scenarioName()).isEqualTo(testCase.metadata().title());
                assertThat(report.execution()).satisfies(executionSummary -> {
                    assertThat(executionSummary.executionId()).isEqualTo(666L);
                    assertThat(executionSummary.time()).isAfter(beforeStartExecution);
                    assertThat(executionSummary.status()).isEqualTo(RUNNING);
                    assertThat(executionSummary.environment()).isEqualTo("env");
                    assertThat(executionSummary.user()).isEqualTo(campaignReport.userId);
                    assertThat(executionSummary.dataset()).hasValue(campaignReport.dataset);
                });
            });
        }

        @Test
        void end_scenario_execution() {
            // Given
            CampaignExecution campaignReport = CampaignExecutionReportBuilder.builder()
                .userId("")
                .build();
            TestCase testCase = buildTestCase("1", "title");
            var testcaseToExecute = new TestCaseDataset(testCase, null);
            campaignReport.addScenarioExecution(singletonList(testcaseToExecute), "env");
            campaignReport.startScenarioExecution(testcaseToExecute, "env");

            ScenarioExecutionCampaign scenarioReport_SUCCESS = buildScenarioReportFromMockedExecution(testCase.id(), null, testCase.metadata().title(), SUCCESS);

            // When
            campaignReport.endScenarioExecution(scenarioReport_SUCCESS);

            // Then
            assertThat(campaignReport.scenarioExecutionReports()).hasSize(1);
            assertThat(campaignReport.scenarioExecutionReports().get(0).execution().status()).isEqualTo(SUCCESS);
        }

        @Test
        void compute_status_from_scenarios_when_end_campaign_execution() {
            // Given
            CampaignExecution campaignReport = CampaignExecutionReportBuilder.builder()
                .userId("")
                .build();
            addScenarioExecutions(campaignReport, "1", "title1", SUCCESS);
            addScenarioExecutions(campaignReport, "2", "title2", FAILURE);

            // When
            campaignReport.endCampaignExecution();

            // Then
            assertThat(campaignReport.scenarioExecutionReports()).hasSize(2);
            assertThat(campaignReport.status()).isEqualTo(FAILURE);
        }

        @Test
        void compute_stop_final_status_when_having_not_executed_scenario() {
            // Given
            CampaignExecution campaignReport = CampaignExecutionReportBuilder.builder()
                .userId("")
                .build();
            addScenarioExecutions(campaignReport, "1", "title1", SUCCESS);
            addScenarioExecutions(campaignReport, "2", "title2", NOT_EXECUTED);

            // When
            campaignReport.endCampaignExecution();

            // Then
            assertThat(campaignReport.scenarioExecutionReports()).hasSize(2);
            assertThat(campaignReport.status()).isEqualTo(STOPPED);
        }
    }

    @Test
    void compute_status_without_retry_scenario_execution() {
        // Given
        String scenarioId = "1";
        ExecutionHistory.ExecutionSummary firstExecution = ImmutableExecutionHistory.ExecutionSummary.builder()
            .executionId(1L)
            .testCaseTitle("")
            .time(LocalDateTime.now().minusMinutes(1L))
            .duration(0L)
            .environment("")
            .user("")
            .status(FAILURE)
            .scenarioId(scenarioId)
            .build();
        ScenarioExecutionCampaign firstReport = new ScenarioExecutionCampaign(scenarioId, "", firstExecution);
        ExecutionHistory.ExecutionSummary retryExecution = ImmutableExecutionHistory.ExecutionSummary.builder()
            .executionId(2L)
            .testCaseTitle("")
            .time(LocalDateTime.now())
            .duration(0L)
            .environment("")
            .user("")
            .status(SUCCESS)
            .scenarioId(scenarioId)
            .build();
        ScenarioExecutionCampaign retryReport = new ScenarioExecutionCampaign(scenarioId, "", retryExecution);
        CampaignExecution campaignReport = CampaignExecutionReportBuilder.builder()
            .addScenarioExecutionReport(firstReport)
            .addScenarioExecutionReport(retryReport)
            .build();

        // When
        ServerReportStatus status = campaignReport.status();

        // Then
        assertThat(status).isEqualTo(SUCCESS);
    }

    @Nested
    @DisplayName("Duration computation")
    class Duration {
        @Test
        void for_one_scenario_execution() {
            LocalDateTime now = LocalDateTime.now();
            long duration = 3;

            List<ScenarioExecutionCampaign> executions = stubScenarioExecution(singletonList(now), singletonList(duration));
            CampaignExecution sut = fakeCampaignReport(executions);

            assertThat(sut.getDuration()).isEqualTo(3);
        }

        @Test
        void for_two_scenarios_sequential_executions() {
            LocalDateTime now = LocalDateTime.now();
            long duration1 = 3;
            LocalDateTime scenarioExecutionStartDate2 = now.plus(duration1, ChronoUnit.MILLIS);
            long duration2 = 6;

            List<ScenarioExecutionCampaign> executions =
                stubScenarioExecution(
                    asList(now, scenarioExecutionStartDate2),
                    asList(duration1, duration2)
                );
            CampaignExecution sut = fakeCampaignReport(executions);

            assertThat(sut.getDuration()).isEqualTo(9);
        }

        @Test
        void for_two_scenarios_parallel_executions() {
            LocalDateTime now = LocalDateTime.now();
            long duration1 = 3;
            long duration2 = 6;

            List<ScenarioExecutionCampaign> executions =
                stubScenarioExecution(
                    asList(now, now),
                    asList(duration1, duration2)
                );
            CampaignExecution sut = fakeCampaignReport(executions);

            assertThat(sut.getDuration()).isEqualTo(6);
        }
    }

    @Test
    void compute_failed_scenarios_executions() {
        CampaignExecution sut = CampaignExecutionReportBuilder.builder()
            .userId("")
            .build();
        addScenarioExecutions(sut, "1", new ExternalDataset("dataset_1"), "", SUCCESS);
        addScenarioExecutions(sut, "2", new ExternalDataset("dataset_2"), "", FAILURE);
        addScenarioExecutions(sut, "3", new ExternalDataset("dataset_1"), "", SUCCESS);

        assertThat(sut.failedScenarioExecutions()).hasSize(1).singleElement()
            .isInstanceOf(ScenarioExecutionCampaign.class)
            .hasFieldOrPropertyWithValue("scenarioId", "2")
            .returns(Optional.of("dataset_2"), sec -> sec.execution().dataset().map(ExternalDataset::getDatasetId));
    }

    @Test
    void compute_execution_without_retries() {
        CampaignExecution sut = CampaignExecutionReportBuilder.builder()
            .userId("")
            .build();
        ExternalDataset dataset1 = new ExternalDataset("dataset_1");
        ExternalDataset dataset2 = new ExternalDataset("dataset_2");
        addScenarioExecutions(sut, "1", dataset1, "", FAILURE);
        addScenarioExecutions(sut, "1", dataset2, "", FAILURE);
        var reportWithDataset1 = addScenarioExecutions(sut, "1", dataset1, "", SUCCESS);
        var reportWithDataset2 = addScenarioExecutions(sut, "1", dataset2, "", SUCCESS);

        CampaignExecution withoutRetries = sut.withoutRetries();

        assertThat(withoutRetries.scenarioExecutionReports())
            .containsExactly(reportWithDataset1, reportWithDataset2);
    }

    private List<ScenarioExecutionCampaign> stubScenarioExecution(List<LocalDateTime> times, List<Long> durations) {
        ExecutionHistory.ExecutionSummary execution = mock(ExecutionHistory.ExecutionSummary.class);
        when(execution.time()).thenReturn(times.get(0), times.subList(1, durations.size()).toArray(new LocalDateTime[0]));
        when(execution.duration()).thenReturn(durations.get(0), durations.subList(1, durations.size()).toArray(new Long[0]));

        ScenarioExecutionCampaign dto = new ScenarioExecutionCampaign("0", UUID.randomUUID().toString(), execution);
        List<ScenarioExecutionCampaign> reports = new ArrayList<>();
        IntStream.range(0, times.size()).forEach(i -> reports.add(dto));
        return reports;
    }

    private CampaignExecution fakeCampaignReport(List<ScenarioExecutionCampaign> executions) {
        return CampaignExecutionReportBuilder.builder()
            .userId("")
            .scenarioExecutionReport(executions)
            .build();
    }

    private void addScenarioExecutions(CampaignExecution campaignReport, String scenarioId, String scenarioTitle, ServerReportStatus scenarioExecutionStatus) {
        addScenarioExecutions(campaignReport, scenarioId, null, scenarioTitle, scenarioExecutionStatus);
    }

    private ScenarioExecutionCampaign addScenarioExecutions(CampaignExecution campaignReport, String scenarioId, ExternalDataset dataset, String scenarioTitle, ServerReportStatus scenarioExecutionStatus) {
        TestCase testCase = buildTestCase(scenarioId, scenarioTitle);
        var testcaseToExecute = new TestCaseDataset(testCase, dataset);
        campaignReport.addScenarioExecution(singletonList(testcaseToExecute), "");
        campaignReport.startScenarioExecution(testcaseToExecute, "");

        ScenarioExecutionCampaign scenarioReport = buildScenarioReportFromMockedExecution(scenarioId, dataset, scenarioTitle, scenarioExecutionStatus);

        campaignReport.endScenarioExecution(scenarioReport);

        assertThat(campaignReport.status()).isEqualTo(RUNNING);
        return scenarioReport;
    }

    private ScenarioExecutionCampaign buildScenarioReportFromMockedExecution(String scenarioId, ExternalDataset dataset, String scenarioTitle, ServerReportStatus scenarioExecutionStatus) {
        ExecutionHistory.ExecutionSummary execution = mock(ExecutionHistory.ExecutionSummary.class);
        when(execution.status()).thenReturn(scenarioExecutionStatus);
        when(execution.dataset()).thenReturn(Optional.ofNullable(dataset));
        when(execution.time()).thenReturn(LocalDateTime.now());
        return new ScenarioExecutionCampaign(scenarioId, scenarioTitle, execution);
    }

    private TestCase buildTestCase(String scenarioId, String scenarioTitle) {
        return GwtTestCase.builder()
            .withMetadata(
                TestCaseMetadataImpl.builder()
                    .withId(scenarioId)
                    .withTitle(scenarioTitle)
                    .build())
            .build();
    }
}
