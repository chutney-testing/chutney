/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.execution.domain.campaign;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.of;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.WaitUtils.awaitDuring;

import com.chutneytesting.campaign.domain.CampaignExecutionRepository;
import com.chutneytesting.campaign.domain.CampaignNotFoundException;
import com.chutneytesting.campaign.domain.CampaignRepository;
import com.chutneytesting.dataset.domain.DataSetRepository;
import com.chutneytesting.jira.api.JiraXrayEmbeddedApi;
import com.chutneytesting.jira.api.ReportForJira;
import com.chutneytesting.scenario.domain.gwt.GwtTestCase;
import com.chutneytesting.server.core.domain.dataset.DataSet;
import com.chutneytesting.server.core.domain.execution.ExecutionRequest;
import com.chutneytesting.server.core.domain.execution.ScenarioExecutionEngine;
import com.chutneytesting.server.core.domain.execution.ScenarioExecutionEngineAsync;
import com.chutneytesting.server.core.domain.execution.history.ExecutionHistory;
import com.chutneytesting.server.core.domain.execution.history.ExecutionHistoryRepository;
import com.chutneytesting.server.core.domain.execution.history.ImmutableExecutionHistory;
import com.chutneytesting.server.core.domain.execution.report.ScenarioExecutionReport;
import com.chutneytesting.server.core.domain.execution.report.ServerReportStatus;
import com.chutneytesting.server.core.domain.instrument.ChutneyMetrics;
import com.chutneytesting.server.core.domain.scenario.TestCase;
import com.chutneytesting.server.core.domain.scenario.TestCaseMetadataImpl;
import com.chutneytesting.server.core.domain.scenario.TestCaseRepository;
import com.chutneytesting.server.core.domain.scenario.campaign.Campaign;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecution;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecutionReportBuilder;
import com.chutneytesting.server.core.domain.scenario.campaign.ScenarioExecutionCampaign;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;
import org.springframework.core.task.support.ExecutorServiceAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StopWatch;

public class CampaignExecutionEngineTest {

    private static ExecutorService executorService;

    private CampaignExecutionEngine sut;

    private final CampaignRepository campaignRepository = mock(CampaignRepository.class);
    private final CampaignExecutionRepository campaignExecutionRepository = mock(CampaignExecutionRepository.class);
    private final ScenarioExecutionEngine scenarioExecutionEngine = mock(ScenarioExecutionEngine.class);
    private final ScenarioExecutionEngineAsync scenarioExecutionEngineAsync = mock(ScenarioExecutionEngineAsync.class);
    private final ExecutionHistoryRepository executionHistoryRepository = mock(ExecutionHistoryRepository.class);
    private final TestCaseRepository testCaseRepository = mock(TestCaseRepository.class);
    private final JiraXrayEmbeddedApi jiraXrayPlugin = mock(JiraXrayEmbeddedApi.class);
    private final ChutneyMetrics metrics = mock(ChutneyMetrics.class);
    private final DataSetRepository datasetRepository = mock(DataSetRepository.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();


    private GwtTestCase firstTestCase;
    private GwtTestCase secondTestCase;
    Long firstScenarioExecutionId = 10L;
    Long secondScenarioExecutionId = 20L;

    @BeforeAll
    public static void setUpAll() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(2);
        taskExecutor.setMaxPoolSize(2);
        taskExecutor.initialize();
        executorService = new ExecutorServiceAdapter(taskExecutor);
    }

    @BeforeEach
    public void setUp() {
        sut = new CampaignExecutionEngine(campaignRepository, campaignExecutionRepository, scenarioExecutionEngine, scenarioExecutionEngineAsync, executionHistoryRepository, testCaseRepository, jiraXrayPlugin, metrics, executorService, datasetRepository, objectMapper);
        firstTestCase = createGwtTestCase("1");
        secondTestCase = createGwtTestCase("2");
        when(testCaseRepository.findExecutableById(firstTestCase.id())).thenReturn(of(firstTestCase));
        when(testCaseRepository.findExecutableById(secondTestCase.id())).thenReturn(of(secondTestCase));
        when(executionHistoryRepository.getExecution(eq(firstTestCase.id()), or(eq(0L), eq(10L))))
            .thenReturn(executionWithId(firstTestCase.id(), firstScenarioExecutionId));
        when(executionHistoryRepository.getExecution(eq(secondTestCase.id()), or(eq(0L), eq(20L))))
            .thenReturn(executionWithId(secondTestCase.id(), secondScenarioExecutionId));
    }

    @Test
    public void should_update_jira_xray() {
        // Given
        Campaign campaign = createCampaign(firstTestCase, secondTestCase);
        when(scenarioExecutionEngine.execute(any(ExecutionRequest.class))).thenReturn(mock(ScenarioExecutionReport.class));

        // When
        CampaignExecution cer = sut.executeScenarioInCampaign(campaign, "user", null);

        ArgumentCaptor<ReportForJira> reportForJiraCaptor = ArgumentCaptor.forClass(ReportForJira.class);
        verify(jiraXrayPlugin).updateTestExecution(eq(campaign.id), eq(cer.executionId), eq(firstTestCase.metadata.id), eq(""), reportForJiraCaptor.capture());

        assertThat(reportForJiraCaptor).isNotNull();

    }

    @Test
    public void should_execute_scenarios_in_sequence_and_store_reports_in_campaign_report_when_executed() {
        // Given
        Campaign campaign = createCampaign(firstTestCase, secondTestCase);

        when(scenarioExecutionEngine.execute(any(ExecutionRequest.class))).thenReturn(mock(ScenarioExecutionReport.class));

        // When
        CampaignExecution campaignExecution = sut.executeScenarioInCampaign(campaign, "user", null);

        // Then
        verify(testCaseRepository, times(2)).findExecutableById(anyString());
        verify(scenarioExecutionEngine, times(2)).execute(any(ExecutionRequest.class));
        verify(executionHistoryRepository, times(4)).getExecution(anyString(), anyLong());

        assertThat(campaignExecution.scenarioExecutionReports()).hasSize(campaign.scenarios.size());
        assertThat(campaignExecution.scenarioExecutionReports().get(0).execution().executionId()).isEqualTo(firstScenarioExecutionId);
        assertThat(campaignExecution.scenarioExecutionReports().get(1).execution().executionId()).isEqualTo(secondScenarioExecutionId);
        assertThat(campaignExecution.partialExecution).isFalse();
        verify(campaignExecutionRepository).saveCampaignExecution(campaign.id, campaignExecution);
        verify(metrics).onCampaignExecutionEnded(
            eq(campaign),
            eq(campaignExecution)
        );
    }

    @Test
    public void should_execute_partially_scenarios_requested() {
        // Given
        Campaign campaign = createCampaign(createGwtTestCase("not executed test case"), secondTestCase);

        when(scenarioExecutionEngine.execute(any(ExecutionRequest.class))).thenReturn(mock(ScenarioExecutionReport.class));

        // When
        CampaignExecution campaignExecution = sut.executeScenarioInCampaign(singletonList(new ScenarioExecutionCampaign("2", secondTestCase.metadata.title, executionWithId("2", 2L).summary())), campaign, "user", null);

        // Then
        verify(testCaseRepository).findExecutableById(anyString());
        verify(scenarioExecutionEngine).execute(any(ExecutionRequest.class));
        verify(executionHistoryRepository, times(2)).getExecution(anyString(), anyLong());

        assertThat(campaignExecution.scenarioExecutionReports()).hasSize(1);
        assertThat(campaignExecution.scenarioExecutionReports().get(0).execution().executionId()).isEqualTo(secondScenarioExecutionId);
        assertThat(campaignExecution.partialExecution).isTrue();
        verify(campaignExecutionRepository).saveCampaignExecution(campaign.id, campaignExecution);
    }

    @Test
    public void should_stop_execution_of_scenarios_when_requested() {
        // Given
        Campaign campaign = createCampaign(firstTestCase, secondTestCase);

        when(scenarioExecutionEngine.execute(any(ExecutionRequest.class))).then((Answer<ScenarioExecutionReport>) invocationOnMock -> {
            awaitDuring(1, SECONDS);
            return mock(ScenarioExecutionReport.class);
        });

        Long firstScenarioExecutionId = 10L;
        var firstScenarioExecution = executionWithId(firstTestCase.id(), firstScenarioExecutionId);
        when(executionHistoryRepository.getExecution(eq(firstTestCase.id()), or(eq(0L), eq(10L))))
            .thenReturn(firstScenarioExecution);

        CampaignExecution campaignExecution = mock(CampaignExecution.class);
        when(campaignExecution.scenarioExecutionReports())
            .thenReturn(List.of(
                new ScenarioExecutionCampaign(firstTestCase.id(), firstScenarioExecution.testCaseTitle(), executionWithId(firstTestCase.id(), firstScenarioExecutionId, ServerReportStatus.RUNNING).summary())
            ));
        when(campaignExecutionRepository.getCampaignExecutionById(anyLong()))
            .thenReturn(campaignExecution);

        // When
        AtomicReference<CampaignExecution> campaignExecutionReport = new AtomicReference<>();

        Executors.newFixedThreadPool(1).submit(() -> campaignExecutionReport.set(sut.executeScenarioInCampaign(campaign, "user")));

        awaitDuring(500, MILLISECONDS);
        sut.stopExecution(0L);
        awaitDuring(1, SECONDS);

        // Then
        verify(scenarioExecutionEngine).execute(any(ExecutionRequest.class));
        verify(executionHistoryRepository, times(2)).getExecution(anyString(), anyLong());
        verify(campaignExecutionRepository).getCampaignExecutionById(0L);
        verify(scenarioExecutionEngineAsync).stop(firstTestCase.id(), firstScenarioExecutionId);

        assertThat(campaignExecutionReport.get().status()).isEqualTo(ServerReportStatus.STOPPED);
        assertThat(campaignExecutionReport.get().scenarioExecutionReports()).hasSize(2);
        assertThat(campaignExecutionReport.get().scenarioExecutionReports().get(0).status()).isEqualTo(ServerReportStatus.SUCCESS);
        assertThat(campaignExecutionReport.get().scenarioExecutionReports().get(1).status()).isEqualTo(ServerReportStatus.NOT_EXECUTED);
        assertThat(campaignExecutionReport.get().scenarioExecutionReports()).hasSize(2);
        assertThat(campaignExecutionReport.get().scenarioExecutionReports().get(0).execution().executionId()).isEqualTo(firstScenarioExecutionId);
        assertThat(campaignExecutionReport.get().scenarioExecutionReports().get(1).execution().executionId()).isEqualTo(-1L);
    }

    @Test
    public void should_retry_failed_scenario() {
        // Given
        Campaign campaign = createCampaign(firstTestCase, secondTestCase, true);

        when(scenarioExecutionEngine.execute(any(ExecutionRequest.class))).thenReturn(mock(ScenarioExecutionReport.class));
        when(executionHistoryRepository.getExecution(eq(firstTestCase.id()), or(eq(0L), eq(10L)))).thenReturn(failedExecutionWithId(firstTestCase.id(), 10L));
        when(executionHistoryRepository.getExecution(eq(secondTestCase.id()), or(eq(0L), eq(20L)))).thenReturn(failedExecutionWithId(secondTestCase.id(), 20L));

        // When
        sut.executeScenarioInCampaign(campaign, "user");

        // Then
        verify(scenarioExecutionEngine, times(4)).execute(any(ExecutionRequest.class));
    }

    @Test
    public void should_execute_scenario_in_parallel() {
        // Given
        Campaign campaign = createCampaign(firstTestCase, secondTestCase, true, false);

        when(scenarioExecutionEngine.execute(any(ExecutionRequest.class))).then((Answer<ScenarioExecutionReport>) invocationOnMock -> {
            awaitDuring(1, SECONDS);
            return mock(ScenarioExecutionReport.class);
        });
        when(executionHistoryRepository.getExecution(eq(firstTestCase.id()), or(eq(0L), eq(10L)))).thenReturn(failedExecutionWithId(firstTestCase.id(), 10L));
        when(executionHistoryRepository.getExecution(eq(secondTestCase.id()), or(eq(0L), eq(20L)))).thenReturn(failedExecutionWithId(secondTestCase.id(), 20L));

        // When
        StopWatch watch = new StopWatch();
        watch.start();
        sut.executeScenarioInCampaign(campaign, "user");
        watch.stop();

        // Then
        verify(scenarioExecutionEngine, times(2)).execute(any(ExecutionRequest.class));
        assertThat(watch.getTotalTimeSeconds()).isLessThan(1.9);
    }

    @Test
    public void should_throw_when_no_campaign_found_on_execute_by_id() {
        when(campaignRepository.findById(anyLong())).thenReturn(null);
        assertThatThrownBy(() -> sut.executeById(generateId(), ""))
            .isInstanceOf(CampaignNotFoundException.class);
    }

    @Test
    public void should_throw_when_campaign_already_running_on_the_same_env() {
        Campaign campaign = createCampaign(firstTestCase, secondTestCase);

        CampaignExecution mockReport = CampaignExecutionReportBuilder.builder()
            .environment(campaign.executionEnvironment())
            .userId("")
            .status(ServerReportStatus.RUNNING)
            .build();
        when(campaignExecutionRepository.currentExecutions(campaign.id)).thenReturn(List.of(mockReport));

        // When
        assertThatThrownBy(() -> sut.executeScenarioInCampaign(campaign, "user"))
            .isInstanceOf(CampaignAlreadyRunningException.class);
    }

    @Test
    public void should_throw_when_campaign_is_empty() {
        Campaign campaign = createCampaign();

        // When
        assertThatThrownBy(() -> sut.executeScenarioInCampaign(campaign, "user"))
            .isInstanceOf(CampaignEmptyExecutionException.class);
    }

    @Test
    public void should_throw_when_replay_is_empty() {
        when(campaignExecutionRepository.getCampaignExecutionById(1L)).thenReturn(
            CampaignExecutionReportBuilder.builder()
                .addScenarioExecutionReport(
                    new ScenarioExecutionCampaign("1", "", executionWithId("1", 1L).summary())
                )
                .build()
        );

        assertThatThrownBy(() -> sut.replayFailedScenariosExecutionsForExecution(1L, ""))
            .isInstanceOf(CampaignEmptyExecutionException.class);
    }

    @Test
    public void should_execute_campaign_in_parallel_on_two_different_envs() {
        String otherEnv = "otherEnv";
        Campaign campaign = createCampaign(firstTestCase, secondTestCase);

        CampaignExecution mockReport = CampaignExecutionReportBuilder.builder()
            .environment(otherEnv)
            .userId("")
            .build();
        when(campaignExecutionRepository.currentExecutions(anyLong())).thenReturn(List.of(mockReport));

        // When
        assertDoesNotThrow(() -> sut.executeScenarioInCampaign(campaign, "user"));
    }

    @Test
    public void should_generate_campaign_execution_id_when_executed() {
        // Given
        Campaign campaign = createCampaign(firstTestCase, secondTestCase);

        when(campaignRepository.findByName(campaign.title)).thenReturn(singletonList(campaign));
        when(campaignRepository.findById(campaign.id)).thenReturn(campaign);

        // When
        sut.executeById(campaign.id, "");
        sut.executeByName(campaign.title, null, "");

        // Then
        verify(campaignRepository).findById(campaign.id);
        verify(campaignRepository).findByName(campaign.title);

        verify(campaignExecutionRepository, times(2)).generateCampaignExecutionId(campaign.id, campaign.executionEnvironment());
    }


    @Test
    public void should_return_last_existing_campaign_execution_for_existing_campaign() {
        // Given
        Campaign campaign = createCampaign();
        CampaignExecution campaignExecution = CampaignExecutionReportBuilder.builder()
            .executionId(123L)
            .campaignId(campaign.id)
            .environment(campaign.executionEnvironment())
            .build();


        when(campaignRepository.findById(campaign.id)).thenReturn(campaign);
        when(campaignExecutionRepository.getLastExecution(campaign.id)).thenReturn(campaignExecution);

        // When
        CampaignExecution result = sut.getLastCampaignExecution(campaign.id);

        // Then
        verify(campaignRepository).findById(campaign.id);
        verify(campaignExecutionRepository).getLastExecution(campaign.id);

        assertThat(result).isEqualTo(campaignExecution);
    }

    @Test
    public void should_throw_exception_when_campaign_does_not_exists() {
        // Given
        Campaign campaign = createCampaign();

        when(campaignRepository.findById(campaign.id)).thenReturn(null);

        // When
        assertThatThrownBy(() -> sut.getLastCampaignExecution(campaign.id));

        // Then
        verify(campaignRepository).findById(campaign.id);
    }

    @Test
    public void should_execute_campaign_with_given_environment_when_executed_by_id() {
        // Given
        Campaign campaign = createCampaign(firstTestCase, secondTestCase);
        when(campaignRepository.findById(campaign.id)).thenReturn(campaign);

        // When
        String executionEnv = "executionEnv";
        String executionUser = "executionUser";
        sut.executeById(campaign.id, executionEnv, null, executionUser);

        // Then
        verify(campaignRepository).findById(campaign.id);
        assertThat(campaign.executionEnvironment()).isEqualTo(executionEnv);
    }

    @Test
    public void should_execute_campaign_with_given_dataset_when_executed_by_id() {
        // Given
        Campaign campaign = createCampaign(firstTestCase, secondTestCase);
        when(campaignRepository.findById(campaign.id)).thenReturn(campaign);

        // When
        DataSet executionDataset = DataSet.builder().withName("executionDataset").withId("executionDataset").build();
        String executionUser = "executionUser";
        CampaignExecution execution = sut.executeById(campaign.id, null, executionDataset, executionUser);

        // Then
        verify(campaignRepository).findById(campaign.id);
        assertThat(execution.dataset.id).isEqualTo(executionDataset.id);
    }

    @Test
    public void should_execute_campaign_with_given_environment_when_executed_by_name() {
        // Given
        Campaign campaign = createCampaign(firstTestCase, secondTestCase);
        when(campaignRepository.findByName(anyString())).thenReturn(singletonList(campaign));

        // When
        String executionEnv = "executionEnv";
        String executionUser = "executionUser";
        sut.executeByName(campaign.title, executionEnv, null, executionUser);

        // Then
        verify(campaignRepository).findByName(campaign.title);
        assertThat(campaign.executionEnvironment()).isEqualTo(executionEnv);
    }

    @Test
    public void should_execute_campaign_with_given_dataset_when_executed_by_name() {
        // Given
        Campaign campaign = createCampaign(firstTestCase, secondTestCase);
        when(campaignRepository.findByName(anyString())).thenReturn(singletonList(campaign));

        // When
        DataSet executionDataset = DataSet.builder().withName("executionDataset").withId("executionDataset").build();
        String executionUser = "executionUser";
        List<CampaignExecution> campaignExecutions = sut.executeByName(campaign.title, null, executionDataset, executionUser);

        // Then
        verify(campaignRepository).findByName(campaign.title);
        assertThat(campaignExecutions).hasSize(1);
        assertThat(campaignExecutions.get(0).dataset.id).isEqualTo(executionDataset.id);
    }

    @Test
    public void should_retrieve_current_campaign_execution_on_a_given_env() {
        String env = "env";
        CampaignExecution report = CampaignExecutionReportBuilder.builder()
            .executionId(1L)
            .campaignId(33L)
            .environment(env)
            .build();
        String otherEnv = "otherEnv";
        CampaignExecution report2 = CampaignExecutionReportBuilder.builder()
            .executionId(2L)
            .campaignId(33L)
            .environment(otherEnv)
            .build();
        CampaignExecution report3 = CampaignExecutionReportBuilder.builder()
            .executionId(3L)
            .campaignId(42L)
            .environment(otherEnv)
            .build();
        when(campaignExecutionRepository.currentExecutions(33L)).thenReturn(List.of(report, report2));
        when(campaignExecutionRepository.currentExecutions(42L)).thenReturn(List.of(report3));

        Optional<CampaignExecution> campaignExecutionReport = sut.currentExecution(33L, env);

        assertThat(campaignExecutionReport).isNotEmpty();
        assertThat(campaignExecutionReport.get().executionId).isEqualTo(1L);
        assertThat(campaignExecutionReport.get().executionEnvironment).isEqualTo(env);
    }

    @Test
    public void should_throw_when_stop_unknown_campaign_execution() {
        assertThatThrownBy(() -> sut.stopExecution(generateId()))
            .isInstanceOf(CampaignExecutionNotFoundException.class);
    }

    @Test
    public void should_throw_when_execute_unknown_campaign_execution() {
        assertThatThrownBy(() -> sut.executeById(generateId(), ""))
            .isInstanceOf(CampaignNotFoundException.class);
    }

    @Test
    public void should_use_campaign_default_dataset_before_execution_when_scenario_in_campaign_does_not_define_dataset() {
        // Given
        TestCase gwtTestCase = GwtTestCase.builder()
            .withMetadata(
                TestCaseMetadataImpl.builder()
                    .withId("gwt")
                    .build()
            )
            .build();

        var campaignScenarios = singletonList(new Campaign.CampaignScenario(gwtTestCase.id(), null));
        Campaign campaign = new Campaign(generateId(), "...", null, campaignScenarios, "...", false, false, "campaignDataSet", List.of("TAG"));

        when(campaignRepository.findById(campaign.id)).thenReturn(campaign);
        when(testCaseRepository.findExecutableById(gwtTestCase.id())).thenReturn(of(gwtTestCase));
        when(scenarioExecutionEngine.execute(any(ExecutionRequest.class))).thenReturn(mock(ScenarioExecutionReport.class));
        when(executionHistoryRepository.getExecution(any(), any())).thenReturn(executionWithId(gwtTestCase.id(), 42L));

        when(datasetRepository.findById(eq("campaignDataSet"))).thenReturn(DataSet.builder().withName("campaignDataSet").build());

        // When
        sut.executeById(campaign.id, "user");

        // Then
        ArgumentCaptor<ExecutionRequest> argumentCaptor = ArgumentCaptor.forClass(ExecutionRequest.class);
        verify(scenarioExecutionEngine).execute(argumentCaptor.capture());
        ExecutionRequest executionRequest = argumentCaptor.getValue();
        assertThat(executionRequest.dataset).isNotNull();
        assertThat(executionRequest.dataset.name).isEqualTo("campaignDataSet");
        assertThat(executionRequest.tags).containsExactly("TAG");
    }

    @Test
    public void should_use_scenario_dataset_over_campaign_default_dataset_before_execution_when_scenario_in_campaign_defines_dataset() {
        // Given
        TestCase gwtTestCase = GwtTestCase.builder()
            .withMetadata(
                TestCaseMetadataImpl.builder()
                    .withId("gwt")
                    .withDefaultDataset("scenarioDefaultDataset")
                    .build()
            )
            .build();

        var campaignScenarios = singletonList(new Campaign.CampaignScenario(gwtTestCase.id(), "scenarioInCampaignDataset"));
        Campaign campaign = new Campaign(generateId(), "...", null, campaignScenarios, "...", false, false, "campaignDataSet", List.of("TAG"));

        when(campaignRepository.findById(campaign.id)).thenReturn(campaign);
        when(testCaseRepository.findExecutableById(gwtTestCase.id())).thenReturn(of(gwtTestCase));
        when(scenarioExecutionEngine.execute(any(ExecutionRequest.class))).thenReturn(mock(ScenarioExecutionReport.class));
        when(executionHistoryRepository.getExecution(any(), any())).thenReturn(executionWithId(gwtTestCase.id(), 42L));

        when(datasetRepository.findById(eq("scenarioInCampaignDataset"))).thenReturn(DataSet.builder().withName("scenarioInCampaignDataset").build());

        // When
        sut.executeById(campaign.id, "user");

        // Then
        ArgumentCaptor<ExecutionRequest> argumentCaptor = ArgumentCaptor.forClass(ExecutionRequest.class);
        verify(scenarioExecutionEngine).execute(argumentCaptor.capture());
        ExecutionRequest executionRequest = argumentCaptor.getValue();
        assertThat(executionRequest.dataset).isNotNull();
        assertThat(executionRequest.dataset.name).isEqualTo("scenarioInCampaignDataset");
        assertThat(executionRequest.tags).containsExactly("TAG");
    }

    @Test
    public void should_not_use_scenario_default_dataset_when_campaign_nor_scenario_in_campaign_do_not_define_dataset() {
        // Given
        TestCase gwtTestCase = GwtTestCase.builder()
            .withMetadata(
                TestCaseMetadataImpl.builder()
                    .withId("gwt")
                    .withDefaultDataset("scenarioDefaultDataset")
                    .build()
            )
            .build();

        var campaignScenarios = singletonList(new Campaign.CampaignScenario(gwtTestCase.id(), null));
        Campaign campaign = new Campaign(generateId(), "...", null, campaignScenarios, "...", false, false, null, List.of("TAG"));

        when(campaignRepository.findById(campaign.id)).thenReturn(campaign);
        when(testCaseRepository.findExecutableById(gwtTestCase.id())).thenReturn(of(gwtTestCase));
        when(scenarioExecutionEngine.execute(any(ExecutionRequest.class))).thenReturn(mock(ScenarioExecutionReport.class));
        when(executionHistoryRepository.getExecution(any(), any())).thenReturn(executionWithId(gwtTestCase.id(), 42L));

        // When
        sut.executeById(campaign.id, "user");

        // Then
        ArgumentCaptor<ExecutionRequest> argumentCaptor = ArgumentCaptor.forClass(ExecutionRequest.class);
        verify(scenarioExecutionEngine).execute(argumentCaptor.capture());
        ExecutionRequest executionRequest = argumentCaptor.getValue();
        assertThat(executionRequest.dataset).isEqualTo(DataSet.NO_DATASET);
        assertThat(executionRequest.tags).containsExactly("TAG");
    }

    @Test
    public void should_execute_by_id_with_inline_dataset() {
        // Given
        Long campaignId = 1L;
        String env = "env";
        var scenarios = Lists.list(firstTestCase.id()).stream().map(id -> new Campaign.CampaignScenario(id, null)).toList();
        Campaign campaign = new Campaign(1L, "campaign1", null, scenarios, env, false, false, "UNKNOWN", List.of("TAG"));
        var datatable = List.of(Map.of("HEADER", "VALUE"));
        var constants = Map.of("HEADER", "VALUE");
        DataSet dataSet = DataSet.builder().withName("").withDatatable(datatable).withConstants(constants).build();
        when(campaignRepository.findById(eq(1L))).thenReturn(campaign);

        // When
        CampaignExecution campaignExecution = sut.executeById(campaignId, env, dataSet, "USER");

        // Then
        assertThat(campaignExecution.dataset).isNotNull();
        assertThat(campaignExecution.dataset.id).isNull();
        assertThat(campaignExecution.dataset.constants).isEqualTo(constants);
        assertThat(campaignExecution.dataset.datatable).isEqualTo(datatable);
        assertThat(campaignExecution.scenarioExecutionReports()).isNotEmpty();
        assertThat(campaignExecution.scenarioExecutionReports().get(0).execution().dataset()).isPresent();
        assertThat(campaignExecution.scenarioExecutionReports().get(0).execution().dataset().get().constants).isEqualTo(constants);
        assertThat(campaignExecution.scenarioExecutionReports().get(0).execution().dataset().get().datatable).isEqualTo(datatable);
    }

    @Test
    public void should_execute_by_id_with_known_dataset() {
        // Given
        Long campaignId = 1L;
        String env = "env";
        var scenarios = Lists.list(firstTestCase.id()).stream().map(id -> new Campaign.CampaignScenario(id, null)).toList();
        Campaign campaign = new Campaign(1L, "campaign1", null, scenarios, env, false, false, "UNKNOWN", List.of("TAG"));
        DataSet dataSet = DataSet.builder().withName("").withId("DATASET_ID").build();
        when(campaignRepository.findById(eq(1L))).thenReturn(campaign);
        when(datasetRepository.findById(eq("DATASET_ID"))).thenReturn(DataSet.builder().withName("DATASET_ID").withId("DATASET_ID").build());

        // When
        CampaignExecution campaignExecution = sut.executeById(campaignId, env, dataSet, "USER");

        // Then
        assertThat(campaignExecution.dataset).isNotNull();
        assertThat(campaignExecution.dataset.constants).isEmpty();
        assertThat(campaignExecution.dataset.datatable).isEmpty();
        assertThat(campaignExecution.dataset.id).isNotNull();
        assertThat(campaignExecution.dataset.id).isEqualTo("DATASET_ID");
        assertThat(campaignExecution.scenarioExecutionReports().get(0).execution().dataset()).isPresent();
        assertThat(campaignExecution.scenarioExecutionReports().get(0).execution().dataset().get().id).isEqualTo("DATASET_ID");
    }

    @Test
    public void should_execute_by_id_with_default_dataset() {
        // Given
        Long campaignId = 1L;
        String env = "env";
        var scenarios = Lists.list(firstTestCase.id()).stream().map(id -> new Campaign.CampaignScenario(id, null)).toList();
        Campaign campaign = new Campaign(1L, "campaign1", null, scenarios, env, false, false, "DATASET_ID", List.of("TAG"));
        when(campaignRepository.findById(eq(1L))).thenReturn(campaign);
        when(datasetRepository.findById(eq("DATASET_ID"))).thenReturn(DataSet.builder().withName("DATASET_ID").withId("DATASET_ID").build());

        // When
        CampaignExecution campaignExecution = sut.executeById(campaignId, env, null, "USER");

        // Then
        assertThat(campaign.executionDataset()).isNotNull();
        assertThat(campaignExecution.dataset).isNotNull();
        assertThat(campaignExecution.dataset.constants).isEmpty();
        assertThat(campaignExecution.dataset.datatable).isEmpty();
        assertThat(campaignExecution.dataset.id).isNotNull();
        assertThat(campaignExecution.dataset.id).isEqualTo("DATASET_ID");
    }

    @Test
    public void should_execute_by_id_without_any_dataset() {
        // Given
        Long campaignId = 1L;
        String env = "env";
        var scenarios = Lists.list(firstTestCase.id()).stream().map(id -> new Campaign.CampaignScenario(id, null)).toList();
        Campaign campaign = new Campaign(1L, "campaign1", null, scenarios, env, false, false, null, List.of("TAG"));
        when(campaignRepository.findById(eq(1L))).thenReturn(campaign);

        // When
        CampaignExecution campaignExecution = sut.executeById(campaignId, env, null, "USER");

        // Then
        assertThat(campaign.executionDataset()).isNull();
        assertThat(campaignExecution.dataset).isNull();
    }

    @ParameterizedTest
    @CsvSource({
        "123, DEV, dataset123, user123",
        "123, DEV, , user123"
    })
    void testExecuteScheduledCampaign(Long campaignId, String environment, String datasetId, String userId) {
        //Given
        var scenarios = Lists.list(firstTestCase.id()).stream().map(id -> new Campaign.CampaignScenario(id, null)).toList();
        Campaign campaign = new Campaign(1L, "campaign1", null, scenarios, "DEV", false, false, null, List.of("TAG"));
        when(campaignRepository.findById(any())).thenReturn(campaign);
        if (datasetId != null) {
            when(datasetRepository.findById(datasetId)).thenReturn(DataSet.NO_DATASET);
        }

        // When
        CampaignExecutionEngine spySut = spy(sut);
        spySut.executeScheduledCampaign(campaignId, environment, datasetId, userId);

        // Then
        if (datasetId != null) {
            verify(spySut, times(1)).executeById(campaignId, environment, DataSet.NO_DATASET, userId);
        } else {
            verify(spySut, times(1)).executeById(campaignId, environment, null, userId);
        }
    }

    private final static Random campaignIdGenerator = new Random();

    private Long generateId() {
        return (long) campaignIdGenerator.nextInt(1000);
    }

    private ExecutionHistory.Execution executionWithId(String scenarioId, Long executionId) {
        return executionWithId(scenarioId, executionId, ServerReportStatus.SUCCESS);
    }

    private ExecutionHistory.Execution failedExecutionWithId(String scenarioId, Long executionId) {
        return executionWithId(scenarioId, executionId, ServerReportStatus.FAILURE);
    }

    private ExecutionHistory.Execution executionWithId(String scenarioId, Long executionId, ServerReportStatus status) {
        return ImmutableExecutionHistory.Execution.builder()
            .executionId(executionId)
            .testCaseTitle("...")
            .time(LocalDateTime.now())
            .duration(3L)
            .status(status)
            .report("{\"report\":{\"status\":\"" + status + "\", \"steps\":[]}}")
            .environment("")
            .user("")
            .scenarioId(scenarioId)
            .build();
    }

    private GwtTestCase createGwtTestCase(String id) {
        return GwtTestCase.builder().withMetadata(TestCaseMetadataImpl.builder().withId(id).build()).build();
    }

    private Campaign createCampaign() {
        return new Campaign(generateId(), "...", null, null, "campaignEnv", false, false, null, null);
    }

    private Campaign createCampaign(Long idCampaign, String env) {
        return new Campaign(idCampaign, "campaign1", null, emptyList(), env, false, false, null, null);
    }

    private Campaign createCampaign(TestCase firstTestCase, TestCase secondtTestCase) {
        return createCampaign(firstTestCase, secondtTestCase, false, false);
    }

    private Campaign createCampaign(TestCase firstTestCase, TestCase secondtTestCase, boolean retryAuto) {
        return createCampaign(firstTestCase, secondtTestCase, false, retryAuto);
    }

    private Campaign createCampaign(TestCase firstTestCase, TestCase secondtTestCase, boolean parallelRun, boolean retryAuto) {
        var scenarios = Lists.list(firstTestCase.id(), secondtTestCase.id()).stream().map(id -> new Campaign.CampaignScenario(id, null)).toList();
        return new Campaign(1L, "campaign1", null, scenarios, "env", parallelRun, retryAuto, null, List.of("TAG"));
    }
}
