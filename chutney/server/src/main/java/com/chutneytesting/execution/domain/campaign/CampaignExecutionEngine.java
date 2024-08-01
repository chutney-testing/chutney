/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.execution.domain.campaign;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.chutneytesting.campaign.domain.CampaignExecutionRepository;
import com.chutneytesting.campaign.domain.CampaignNotFoundException;
import com.chutneytesting.campaign.domain.CampaignRepository;
import com.chutneytesting.dataset.domain.DataSetRepository;
import com.chutneytesting.jira.api.JiraXrayEmbeddedApi;
import com.chutneytesting.jira.domain.exception.NoJiraConfigurationException;
import com.chutneytesting.server.core.domain.dataset.DataSet;
import com.chutneytesting.server.core.domain.execution.ExecutionRequest;
import com.chutneytesting.server.core.domain.execution.FailedExecutionAttempt;
import com.chutneytesting.server.core.domain.execution.ScenarioExecutionEngine;
import com.chutneytesting.server.core.domain.execution.history.ExecutionHistory;
import com.chutneytesting.server.core.domain.execution.history.ExecutionHistoryRepository;
import com.chutneytesting.server.core.domain.execution.report.ScenarioExecutionReport;
import com.chutneytesting.server.core.domain.execution.report.ServerReportStatus;
import com.chutneytesting.server.core.domain.instrument.ChutneyMetrics;
import com.chutneytesting.server.core.domain.scenario.ScenarioNotFoundException;
import com.chutneytesting.server.core.domain.scenario.ScenarioNotParsableException;
import com.chutneytesting.server.core.domain.scenario.TestCaseRepository;
import com.chutneytesting.server.core.domain.scenario.campaign.Campaign;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecution;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecutionReportBuilder;
import com.chutneytesting.server.core.domain.scenario.campaign.ScenarioExecutionCampaign;
import com.chutneytesting.server.core.domain.scenario.campaign.TestCaseDataset;
import com.chutneytesting.tools.Try;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Load campaigns with {@link CampaignRepository}
 * Run each scenario with @{@link ScenarioExecutionEngine}
 */
public class CampaignExecutionEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(Campaign.class);

    private final ExecutorService executor;
    private final CampaignRepository campaignRepository;
    private final CampaignExecutionRepository campaignExecutionRepository;
    private final ScenarioExecutionEngine scenarioExecutionEngine;
    private final ExecutionHistoryRepository executionHistoryRepository;
    private final TestCaseRepository testCaseRepository;
    private final JiraXrayEmbeddedApi jiraXrayEmbeddedApi;
    private final ChutneyMetrics metrics;
    private final DataSetRepository datasetRepository;

    private final Map<Long, Boolean> currentCampaignExecutionsStopRequests = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public CampaignExecutionEngine(CampaignRepository campaignRepository,
                                   CampaignExecutionRepository campaignExecutionRepository,
                                   ScenarioExecutionEngine scenarioExecutionEngine,
                                   ExecutionHistoryRepository executionHistoryRepository,
                                   TestCaseRepository testCaseRepository,
                                   JiraXrayEmbeddedApi jiraXrayEmbeddedApi,
                                   ChutneyMetrics metrics,
                                   ExecutorService executorService,
                                   DataSetRepository datasetRepository, ObjectMapper objectMapper) {
        this.campaignRepository = campaignRepository;
        this.campaignExecutionRepository = campaignExecutionRepository;
        this.scenarioExecutionEngine = scenarioExecutionEngine;
        this.executionHistoryRepository = executionHistoryRepository;
        this.testCaseRepository = testCaseRepository;
        this.jiraXrayEmbeddedApi = jiraXrayEmbeddedApi;
        this.metrics = metrics;
        this.executor = executorService;
        this.datasetRepository = datasetRepository;
        this.objectMapper = objectMapper;
    }

    public CampaignExecution getLastCampaignExecution(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId);
        return campaignExecutionRepository.getLastExecution(campaign.id);
    }

    public List<CampaignExecution> executeByNameWithEnvAndDataset(String campaignName, String environment, String dataset, String userId) {
        List<Campaign> campaigns = campaignRepository.findByName(campaignName);
        return campaigns.stream()
            .map(campaign -> selectExecutionEnvironment(campaign, environment))
            .map(campaign -> selectExecutionDataset(campaign, dataset))
            .map(campaign -> executeScenarioInCampaign(campaign, userId))
            .collect(Collectors.toList());
    }

    public List<CampaignExecution> executeByNameWithDataset(String campaignName, String dataset, String userId) {
        return executeByNameWithEnvAndDataset(campaignName, null, dataset, userId);
    }

    public List<CampaignExecution> executeByNameWithEnv(String campaignName, String environment, String userId) {
        return executeByNameWithEnvAndDataset(campaignName, environment, null, userId);
    }

    public List<CampaignExecution> executeByName(String campaignName, String userId) {
        return executeByNameWithEnvAndDataset(campaignName, null, null, userId);
    }

    public CampaignExecution executeByIdWithEnvAndDataset(Long campaignId, String environment, String dataset, String userId) {
        return ofNullable(campaignRepository.findById(campaignId))
            .map(campaign -> selectExecutionEnvironment(campaign, environment))
            .map(campaign -> selectExecutionDataset(campaign, dataset))
            .map(campaign -> executeScenarioInCampaign(campaign, userId))
            .orElseThrow(() -> new CampaignNotFoundException(campaignId));
    }

    public CampaignExecution executeByIdWithDataset(Long campaignId, String dataset, String userId) {
        return executeByIdWithEnvAndDataset(campaignId, null, dataset, userId);
    }

    public CampaignExecution executeByIdWithEnv(Long campaignId, String environment, String userId) {
        return executeByIdWithEnvAndDataset(campaignId, environment, null, userId);
    }

    public CampaignExecution executeById(Long campaignId, String userId) {
        return executeByIdWithEnvAndDataset(campaignId, null, null, userId);
    }

    public Optional<CampaignExecution> currentExecution(Long campaignId, String environment) {
        return campaignExecutionRepository.currentExecutions(campaignId)
            .stream()
            .filter(exec -> exec.executionEnvironment.equals(environment))
            .findAny();
    }


    public void stopExecution(Long executionId) {
        LOGGER.trace("Stop requested for {}", executionId);
        ofNullable(currentCampaignExecutionsStopRequests.computeIfPresent(executionId, (aLong, aBoolean) -> Boolean.TRUE))
            .orElseThrow(() -> new CampaignExecutionNotFoundException(null, executionId));
    }

    public CampaignExecution replayFailedScenariosExecutionsForExecution(Long campaignExecutionId, String userId) {
        CampaignExecution campaignExecution = campaignExecutionRepository.getCampaignExecutionById(campaignExecutionId).withoutRetries();
        List<ScenarioExecutionCampaign> failedExecutions = campaignExecution.failedScenarioExecutions();
        if (failedExecutions.isEmpty()) {
            throw new CampaignEmptyExecutionException(campaignExecution);
        }
        Campaign campaign = campaignRepository.findById(campaignExecution.campaignId);
        campaign.executionEnvironment(campaignExecution.executionEnvironment);
        campaign.executionDataset(campaignExecution.dataSetId);
        return executeScenarioInCampaign(failedExecutions, campaign, userId);
    }

    CampaignExecution executeScenarioInCampaign(Campaign campaign, String userId) {
        return executeScenarioInCampaign(emptyList(), campaign, userId);
    }

    CampaignExecution executeScenarioInCampaign(List<ScenarioExecutionCampaign> failedExecutions, Campaign campaign, String userId) {
        verifyHasScenarios(campaign);
        verifyNotAlreadyRunning(campaign);
        Long executionId = campaignExecutionRepository.generateCampaignExecutionId(campaign.id, campaign.executionEnvironment());

        CampaignExecution campaignExecution = CampaignExecutionReportBuilder.builder()
            .executionId(executionId)
            .campaignId(campaign.id)
            .campaignName(campaign.title)
            .partialExecution(!failedExecutions.isEmpty())
            .environment(campaign.executionEnvironment())
            .dataSetId(isNotBlank(campaign.externalDatasetId) ? campaign.externalDatasetId : null)
            .userId(userId)
            .build();

        campaignExecutionRepository.startExecution(campaign.id, campaignExecution);
        currentCampaignExecutionsStopRequests.put(executionId, Boolean.FALSE);
        try {
            if (failedExecutions.isEmpty()) {
                return execute(campaign, campaignExecution, campaign.scenarios);
            } else {
                var campaignScenarios = failedExecutions.stream()
                    .map(ScenarioExecutionCampaign::execution)
                    .map(sec -> new Campaign.CampaignScenario(sec.scenarioId(), sec.datasetId().orElse(null)))
                    .toList();
                return execute(campaign, campaignExecution, campaignScenarios);
            }
        } catch (Exception e) {
            LOGGER.error("Not managed exception occurred", e);
            throw new RuntimeException(e);
        } finally {
            campaignExecution.endCampaignExecution();
            LOGGER.info("Save campaign {} execution {} with status {}", campaign.id, campaignExecution.executionId, campaignExecution.status());
            currentCampaignExecutionsStopRequests.remove(executionId);
            campaignExecutionRepository.stopExecution(campaign.id, campaign.executionEnvironment());

            Try.exec(() -> {
                campaignExecutionRepository.saveCampaignExecution(campaign.id, campaignExecution);
                return null;
            }).ifFailed(e -> LOGGER.error("Error saving report of campaign {} execution {}", campaign.id, campaignExecution.executionId));

            Try.exec(() -> {
                metrics.onCampaignExecutionEnded(campaign, campaignExecution);
                return null;
            }).ifFailed(e -> LOGGER.error("Error saving metrics for campaign {} execution {}", campaign.id, campaignExecution.executionId));
        }
    }

    private CampaignExecution execute(Campaign campaign, CampaignExecution campaignExecution, List<Campaign.CampaignScenario> scenariosToExecute) {
        LOGGER.trace("Execute campaign {} : {}", campaign.id, campaign.title);
        List<TestCaseDataset> testCaseDatasets = scenariosToExecute.stream()
            .map(cs ->
                testCaseRepository.findExecutableById(cs.scenarioId())
                    .map(tc -> new TestCaseDataset(tc, cs.datasetId()))
            )
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();

        campaignExecution.addScenarioExecution(testCaseDatasets, campaign.executionEnvironment());
        try {
            if (campaign.parallelRun) {
                Collection<Callable<Object>> toExecute = Lists.newArrayList();
                for (TestCaseDataset t : testCaseDatasets) {
                    toExecute.add(Executors.callable(() -> executeScenarioInCampaign(campaign, campaignExecution).accept(t)));
                }
                executor.invokeAll(toExecute);
            } else {
                for (TestCaseDataset t : testCaseDatasets) {
                    executor.invokeAll(singleton(Executors.callable(() -> executeScenarioInCampaign(campaign, campaignExecution).accept(t))));
                }
            }
        } catch (InterruptedException e) {
            LOGGER.error("Error ", e);
        } catch (Exception e) {
            LOGGER.error("Unexpected error ", e);
        }
        return campaignExecution;
    }

    private Consumer<TestCaseDataset> executeScenarioInCampaign(Campaign campaign, CampaignExecution campaignExecution) {
        return testCaseDataset -> {
            ScenarioExecutionCampaign scenarioExecution;
            // Is stop requested ?
            if (!currentCampaignExecutionsStopRequests.get(campaignExecution.executionId)) {
                // Init scenario execution in campaign report
                campaignExecution.startScenarioExecution(testCaseDataset, campaign.executionEnvironment());
                // Execute scenario
                scenarioExecution = executeScenario(campaign, testCaseDataset, campaignExecution);
                // Retry one time if failed
                if (campaign.retryAuto && ServerReportStatus.FAILURE.equals(scenarioExecution.status())) {
                    scenarioExecution = executeScenario(campaign, testCaseDataset, campaignExecution);
                }
            } else {
                scenarioExecution = generateNotExecutedScenarioExecutionAndReport(campaign, testCaseDataset, campaignExecution);
            }
            // Add scenario report to campaign's one
            ofNullable(scenarioExecution)
                .ifPresent(serc -> {
                    campaignExecution.endScenarioExecution(serc);
                    // update xray test
                    ExecutionHistory.Execution execution = executionHistoryRepository.getExecution(serc.scenarioId(), serc.execution().executionId());
                    updateJira(campaign, campaignExecution, serc, execution);
                });
        };
    }

    private void updateJira(Campaign campaign, CampaignExecution campaignExecution, ScenarioExecutionCampaign serc, ExecutionHistory.Execution execution) {
        try {
            jiraXrayEmbeddedApi.updateTestExecution(campaign.id, campaignExecution.executionId, serc.scenarioId(), JiraReportMapper.from(execution.report(), objectMapper));
        } catch (NoJiraConfigurationException e) { // Silent
        } catch (Exception e) {
            LOGGER.warn("Update JIRA failed", e);
        }
    }

    private ScenarioExecutionCampaign generateNotExecutedScenarioExecutionAndReport(Campaign campaign, TestCaseDataset testCaseDataset, CampaignExecution campaignExecution) {
        ExecutionRequest executionRequest = buildExecutionRequest(campaign, testCaseDataset, campaignExecution);
        ExecutionHistory.Execution execution = scenarioExecutionEngine.saveNotExecutedScenarioExecution(executionRequest);
        return new ScenarioExecutionCampaign(testCaseDataset.testcase().id(), testCaseDataset.testcase().metadata().title(), execution.summary());
    }


    private ScenarioExecutionCampaign executeScenario(Campaign campaign, TestCaseDataset testCaseDataset, CampaignExecution campaignExecution) {
        Long executionId;
        String scenarioName;
        try {
            LOGGER.trace("Execute scenario {} for campaign {}", testCaseDataset.testcase().id(), campaign.id);
            ExecutionRequest executionRequest = buildExecutionRequest(campaign, testCaseDataset, campaignExecution);
            ScenarioExecutionReport scenarioExecutionReport = scenarioExecutionEngine.execute(executionRequest);
            executionId = scenarioExecutionReport.executionId;
            scenarioName = scenarioExecutionReport.scenarioName;
        } catch (FailedExecutionAttempt e) {
            LOGGER.warn("Failed execution attempt for scenario {} for campaign {}", testCaseDataset.testcase().id(), campaign.id);
            executionId = e.executionId;
            scenarioName = e.title;
        } catch (ScenarioNotFoundException | ScenarioNotParsableException se) {
            LOGGER.error("Scenario error for scenario {} for campaign {}", testCaseDataset.testcase().id(), campaign.id, se);
            // TODO - Do not hide scenario problem
            return null;
        }
        // TODO - why an extra DB request when we already have the report above ?
        ExecutionHistory.Execution execution = executionHistoryRepository.getExecution(testCaseDataset.testcase().id(), executionId);
        return new ScenarioExecutionCampaign(testCaseDataset.testcase().id(), scenarioName, execution.summary());
    }

    private ExecutionRequest buildExecutionRequest(Campaign campaign, TestCaseDataset testCaseDataset, CampaignExecution campaignExecution) {
        // TODO if dataset null should throw exception ?
        DataSet dataset = ofNullable(testCaseDataset.datasetId())
            .or(() -> ofNullable(campaign.externalDatasetId))
            .map(datasetRepository::findById)
            .orElse(null);
        return new ExecutionRequest(testCaseDataset.testcase(), campaign.executionEnvironment(), campaignExecution.userId, dataset, campaignExecution, campaign.tags);
    }

    private void verifyNotAlreadyRunning(Campaign campaign) {
        Optional<CampaignExecution> currentReport = currentExecution(campaign.id, campaign.executionEnvironment());
        if (currentReport.isPresent() && !currentReport.get().status().isFinal()) {
            throw new CampaignAlreadyRunningException(currentReport.get());
        }
    }

    private void verifyHasScenarios(Campaign campaign) {
        if (campaign.scenarios.isEmpty()) {
            throw new CampaignEmptyExecutionException(campaign);
        }
    }

    private Campaign selectExecutionEnvironment(Campaign campaign, String environment) {
        ofNullable(environment).ifPresent(campaign::executionEnvironment);
        return campaign;
    }

    private Campaign selectExecutionDataset(Campaign campaign, String dataset) {
        ofNullable(dataset).ifPresent(campaign::executionDataset);
        return campaign;
    }
}
