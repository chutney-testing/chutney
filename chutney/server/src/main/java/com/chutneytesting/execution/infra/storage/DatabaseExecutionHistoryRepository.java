/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.execution.infra.storage;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import com.chutneytesting.campaign.infra.CampaignExecutionJpaRepository;
import com.chutneytesting.campaign.infra.CampaignJpaRepository;
import com.chutneytesting.campaign.infra.jpa.CampaignExecutionEntity;
import com.chutneytesting.execution.infra.storage.jpa.ScenarioExecutionEntity;
import com.chutneytesting.execution.infra.storage.jpa.ScenarioExecutionReportEntity;
import com.chutneytesting.server.core.domain.execution.history.ExecutionHistory.DetachedExecution;
import com.chutneytesting.server.core.domain.execution.history.ExecutionHistory.Execution;
import com.chutneytesting.server.core.domain.execution.history.ExecutionHistory.ExecutionSummary;
import com.chutneytesting.server.core.domain.execution.history.ExecutionHistoryRepository;
import com.chutneytesting.server.core.domain.execution.history.ImmutableExecutionHistory;
import com.chutneytesting.server.core.domain.execution.report.ReportNotFoundException;
import com.chutneytesting.server.core.domain.execution.report.ScenarioExecutionReport;
import com.chutneytesting.server.core.domain.execution.report.ServerReportStatus;
import com.chutneytesting.server.core.domain.execution.report.StepExecutionReportCore;
import com.chutneytesting.server.core.domain.scenario.TestCaseRepository;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecution;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
class DatabaseExecutionHistoryRepository implements ExecutionHistoryRepository {

    private final DatabaseExecutionJpaRepository scenarioExecutionsJpaRepository;
    private final ScenarioExecutionReportJpaRepository scenarioExecutionReportJpaRepository;
    private final CampaignJpaRepository campaignJpaRepository;
    private final CampaignExecutionJpaRepository campaignExecutionJpaRepository;
    private final TestCaseRepository testCaseRepository;
    private final ObjectMapper objectMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseExecutionHistoryRepository.class);


    DatabaseExecutionHistoryRepository(
        DatabaseExecutionJpaRepository scenarioExecutionsJpaRepository,
        ScenarioExecutionReportJpaRepository scenarioExecutionReportJpaRepository,
        CampaignJpaRepository campaignJpaRepository, TestCaseRepository testCaseRepository,
        CampaignExecutionJpaRepository campaignExecutionJpaRepository,
        @Qualifier("reportObjectMapper") ObjectMapper objectMapper) {
        this.scenarioExecutionsJpaRepository = scenarioExecutionsJpaRepository;
        this.scenarioExecutionReportJpaRepository = scenarioExecutionReportJpaRepository;
        this.campaignJpaRepository = campaignJpaRepository;
        this.testCaseRepository = testCaseRepository;
        this.campaignExecutionJpaRepository = campaignExecutionJpaRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<String, ExecutionSummary> getLastExecutions(List<String> scenariosIds) {
        List<String> validScenariosIds = scenariosIds.stream().filter(id -> !invalidScenarioId(id)).toList();
        List<Long> executionsIds = scenarioExecutionsJpaRepository.findLastByStatusAndScenariosIds(validScenariosIds, ServerReportStatus.RUNNING)
            .stream().map(t -> t.get(0, Long.class)).toList();
        Iterable<ScenarioExecutionEntity> lastExecutions = scenarioExecutionsJpaRepository.findAllById(executionsIds);
        return StreamSupport.stream(lastExecutions.spliterator(), true)
            .collect(Collectors.toMap(ScenarioExecutionEntity::scenarioId, ScenarioExecutionEntity::toDomain));
    }

    @Override
    public List<ExecutionSummary> getExecutions(String scenarioId) {
        if (invalidScenarioId(scenarioId)) {
            return emptyList();
        }
        List<ScenarioExecutionEntity> scenarioExecutions = scenarioExecutionsJpaRepository.findByScenarioIdOrderByIdDesc(scenarioId);
        return scenarioExecutions.stream()
            .map(this::scenarioExecutionToExecutionSummary)
            .toList();
    }

    @Override
    public List<ExecutionSummary> getExecutions() {
        return scenarioExecutionsJpaRepository.findAll().stream()
            .map(this::scenarioExecutionToExecutionSummary)
            .toList();
    }

    @Override
    public ExecutionSummary getExecutionSummary(Long executionId) {
        return scenarioExecutionsJpaRepository.findById(executionId)
            .map(this::scenarioExecutionToExecutionSummary)
            .orElseThrow(
                () -> new ReportNotFoundException(executionId)
            );
    }

    private ExecutionSummary scenarioExecutionToExecutionSummary(ScenarioExecutionEntity scenarioExecution) {
        CampaignExecution campaignExecution = ofNullable(scenarioExecution.campaignExecution())
            .map(ce -> ce.toDomain(campaignJpaRepository.findById(ce.campaignId()).get().title()))
            .orElse(null);
        return scenarioExecution.toDomain(campaignExecution);
    }

    @Override
    @Transactional
    public Execution store(String scenarioId, DetachedExecution detachedExecution) throws IllegalStateException {
        if (invalidScenarioId(scenarioId)) {
            throw new IllegalStateException("Scenario id is null or empty");
        }
        ScenarioExecutionEntity scenarioExecution = ScenarioExecutionEntity.fromDomain(scenarioId, detachedExecution);
        if (detachedExecution.campaignReport().isPresent()) {
            Optional<CampaignExecutionEntity> campaignExecution = campaignExecutionJpaRepository.findById(detachedExecution.campaignReport().get().executionId);
            scenarioExecution.forCampaignExecution(campaignExecution.get());
        }
        scenarioExecution = scenarioExecutionsJpaRepository.save(scenarioExecution);
        scenarioExecutionReportJpaRepository.save(new ScenarioExecutionReportEntity(scenarioExecution, detachedExecution.report()));
        Execution execution = detachedExecution.attach(scenarioExecution.id(), scenarioId);
        return ImmutableExecutionHistory.Execution.builder().from(execution).build();
    }

    @Override
    // TODO remove scenarioId params
    public Execution getExecution(String scenarioId, Long reportId) throws ReportNotFoundException {
        if (invalidScenarioId(scenarioId) || testCaseRepository.findById(scenarioId).isEmpty()) {
            throw new ReportNotFoundException(scenarioId, reportId);
        }
        return scenarioExecutionReportJpaRepository.findById(reportId).map(ScenarioExecutionReportEntity::toDomain)
            .orElseThrow(
                () -> new ReportNotFoundException(scenarioId, reportId)
            );
    }

    @Override
    public List<ExecutionSummary> getExecutionReportMatchQuery(String query) {
        return scenarioExecutionsJpaRepository
            .getExecutionReportMatchQuery(query)
            .stream()
            .map(this::scenarioExecutionToExecutionSummary)
            .toList();
    }

    @Override
    @Transactional
    public void update(String scenarioId, Execution updatedExecution) throws ReportNotFoundException {
        if (!scenarioExecutionsJpaRepository.existsById(updatedExecution.executionId())) {
            throw new ReportNotFoundException(scenarioId, updatedExecution.executionId());
        }
        update(updatedExecution);
    }

    private void update(Execution updatedExecution) throws ReportNotFoundException {
        ScenarioExecutionEntity execution = scenarioExecutionsJpaRepository.findById(updatedExecution.executionId()).orElseThrow(
            () -> new ReportNotFoundException(updatedExecution.executionId())
        );

        execution.updateFromExecution(updatedExecution);
        scenarioExecutionsJpaRepository.save(execution);
        updateReport(updatedExecution);
    }

    private void updateReport(Execution execution) throws ReportNotFoundException {
        ScenarioExecutionReportEntity scenarioExecutionReport = scenarioExecutionReportJpaRepository.findById(execution.executionId()).orElseThrow(
            () -> new ReportNotFoundException(execution.executionId())
        );
        scenarioExecutionReport.updateReport(execution);
        scenarioExecutionReportJpaRepository.save(scenarioExecutionReport);
    }

    @Override
    @Transactional
    public int setAllRunningExecutionsToKO() {
        List<ExecutionSummary> runningExecutions = getExecutionsWithStatus(ServerReportStatus.RUNNING);
        updateExecutionsToKO(runningExecutions);

        List<ExecutionSummary> pausedExecutions = getExecutionsWithStatus(ServerReportStatus.PAUSED);
        updateExecutionsToKO(pausedExecutions);

        return runningExecutions.size() + pausedExecutions.size();
    }

    @Override
    public List<ExecutionSummary> getExecutionsWithStatus(ServerReportStatus status) {
        return scenarioExecutionsJpaRepository.findByStatus(status).stream().map(ScenarioExecutionEntity::toDomain).toList();
    }

    @Override
    @Transactional
    public void deleteExecutions(Set<Long> executionsIds) {
        Set<Long> campaignExecutionsIds = getCampaignExecutionsWithOnlyOneScenarioExecution(executionsIds);

        campaignExecutionJpaRepository.deleteAllByIdInBatch(campaignExecutionsIds);
        scenarioExecutionReportJpaRepository.deleteAllById(executionsIds);
        scenarioExecutionsJpaRepository.deleteAllByIdInBatch(executionsIds);
    }

    private Set<Long> getCampaignExecutionsWithOnlyOneScenarioExecution(Set<Long> executionsIds) {
        return executionsIds.stream()
            .map(this::getExecutionSummary)
            .map(ExecutionSummary::campaignReport)
            .flatMap(Optional::stream)
            .filter(campaignExecution -> campaignExecution.scenarioExecutionReports().size() == 1)
            .map(campaignExecution -> campaignExecution.executionId)
            .collect(Collectors.toSet());
    }

    private void updateExecutionsToKO(List<ExecutionSummary> executions) {
        executions.stream()
            .map(this::buildKnockoutExecutionFrom)
            .forEach(this::update);
    }

    private ImmutableExecutionHistory.Execution buildKnockoutExecutionFrom(ExecutionSummary executionSummary) {
        String reportStoppedRunningOrPausedStatus = stopRunningOrPausedReport(executionSummary);
        return ImmutableExecutionHistory.Execution.builder()
            .executionId(executionSummary.executionId())
            .status(ServerReportStatus.FAILURE)
            .time(executionSummary.time())
            .duration(executionSummary.duration())
            .info(executionSummary.info())
            .error("Execution was interrupted !")
            .report(reportStoppedRunningOrPausedStatus)
            .testCaseTitle(executionSummary.testCaseTitle())
            .environment(executionSummary.environment())
            .user(executionSummary.user())
            .scenarioId(executionSummary.scenarioId())
            .tags(executionSummary.tags())
            .build();
    }

    private String stopRunningOrPausedReport(ExecutionSummary executionSummary) {
        return scenarioExecutionReportJpaRepository.findById(executionSummary.executionId()).map(ScenarioExecutionReportEntity::toDomain).map(execution -> {
            try {
                ScenarioExecutionReport newScenarioExecutionReport = updateStatusInScenarioExecutionReportWithStoppedStatusIfRunningOrPaused(execution);
                return objectMapper.writeValueAsString(newScenarioExecutionReport);
            } catch (JsonProcessingException exception) {
                LOGGER.error("Unexpected error while deserializing report for execution id " + executionSummary.executionId(), exception);
                return "";
            }
        }).orElseGet(() -> {
            LOGGER.warn("Report not found for execution id {}", executionSummary.executionId());
            return "";
        });
    }

    private ScenarioExecutionReport updateStatusInScenarioExecutionReportWithStoppedStatusIfRunningOrPaused(Execution execution) throws JsonProcessingException {
        ScenarioExecutionReport scenarioExecutionReport = objectMapper.readValue(execution.report(), ScenarioExecutionReport.class);
        StepExecutionReportCore report = updateStepWithStoppedStatusIfRunningOrPaused(scenarioExecutionReport.report);
        return updateScenarioExecutionReport(scenarioExecutionReport, report);
    }

    private ScenarioExecutionReport updateScenarioExecutionReport(ScenarioExecutionReport scenarioExecutionReport, StepExecutionReportCore report) {
        return new ScenarioExecutionReport(
            scenarioExecutionReport.executionId,
            scenarioExecutionReport.scenarioName,
            scenarioExecutionReport.environment,
            scenarioExecutionReport.user,
            scenarioExecutionReport.tags,
            scenarioExecutionReport.constants,
            scenarioExecutionReport.datatable,
            report);
    }

    private List<StepExecutionReportCore> updateStepListWithStoppedStatusIfRunningOrPaused(List<StepExecutionReportCore> steps) {
        return steps.stream().map(this::updateStepWithStoppedStatusIfRunningOrPaused).collect(Collectors.toList());
    }

    private boolean isExecutionRunningOrPaused(ServerReportStatus status) {
        return status.equals(ServerReportStatus.RUNNING) || status.equals(ServerReportStatus.PAUSED);
    }

    private StepExecutionReportCore updateStepWithStoppedStatusIfRunningOrPaused(StepExecutionReportCore step) {
        ServerReportStatus status = isExecutionRunningOrPaused(step.status) ? ServerReportStatus.STOPPED : step.status;
        List<StepExecutionReportCore> steps = updateStepListWithStoppedStatusIfRunningOrPaused(step.steps);
        return new StepExecutionReportCore(
            step.name,
            step.duration,
            step.startDate,
            status,
            step.information,
            step.errors,
            steps,
            step.type,
            step.targetName,
            step.targetUrl,
            step.strategy,
            step.evaluatedInputs,
            step.stepOutputs
        );
    }

    private boolean invalidScenarioId(String scenarioId) {
        return isNullOrEmpty(scenarioId);
    }
}
