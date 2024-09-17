/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.scenario.campaign;

import static com.chutneytesting.server.core.domain.execution.report.ServerReportStatus.RUNNING;
import static com.chutneytesting.server.core.domain.execution.report.ServerReportStatus.SUCCESS;
import static com.chutneytesting.server.core.domain.tools.DatasetUtils.compareDataset;
import static java.time.LocalDateTime.now;
import static java.util.Collections.unmodifiableList;
import static java.util.Optional.ofNullable;

import com.chutneytesting.server.core.domain.dataset.DataSet;
import com.chutneytesting.server.core.domain.execution.history.ExecutionHistory;
import com.chutneytesting.server.core.domain.execution.history.ImmutableExecutionHistory;
import com.chutneytesting.server.core.domain.execution.report.ServerReportStatus;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CampaignExecution {

    // Mandatory
    public final Long executionId;
    public final String campaignName;
    public final boolean partialExecution;
    public final String executionEnvironment;
    public final DataSet dataset;
    public final String userId;

    // Not mandatory
    public final LocalDateTime startDate;
    private ServerReportStatus status;
    private final List<ScenarioExecutionCampaign> scenarioExecutions;
    public final Long campaignId;

    CampaignExecution(
        Long executionId,
        Long campaignId,
        String campaignName,
        boolean partialExecution,
        String executionEnvironment,
        String userId,
        DataSet dataset,
        LocalDateTime startDate,
        ServerReportStatus status,
        List<ScenarioExecutionCampaign> scenarioExecutions
    ) {
        this.executionId = executionId;
        this.campaignId = campaignId;
        this.campaignName = campaignName;
        this.partialExecution = partialExecution;
        this.executionEnvironment = executionEnvironment;
        this.dataset = dataset;
        this.userId = userId;
        this.scenarioExecutions = scenarioExecutions;

        if (scenarioExecutions.isEmpty()) {
            this.startDate = ofNullable(startDate).orElseGet(LocalDateTime::now);
            this.status = ofNullable(status).orElse(SUCCESS);
        } else {
            this.startDate = ofNullable(startDate).orElseGet(() -> findStartDate(scenarioExecutions));
            this.status = ofNullable(status).orElseGet(() -> findStatus(scenarioExecutions));
        }
    }

    public void addScenarioExecution(List<TestCaseDataset> testCaseDatasets, String executionEnvironment) {
        this.status = RUNNING;
        testCaseDatasets.forEach(testCase ->
            this.scenarioExecutions.add(
                new ScenarioExecutionCampaign(
                    testCase.testcase().id(),
                    testCase.testcase().metadata().title(),
                    ImmutableExecutionHistory.ExecutionSummary.builder()
                        .executionId(-1L)
                        .testCaseTitle(testCase.testcase().metadata().title())
                        .time(now())
                        .status(ServerReportStatus.NOT_EXECUTED)
                        .duration(0)
                        .environment(executionEnvironment)
                        .dataset(selectDatasetId(testCase))
                        .user(userId)
                        .scenarioId(testCase.testcase().id())
                        .build())));
    }

    public void startScenarioExecution(TestCaseDataset testCaseDataset, String executionEnvironment) throws UnsupportedOperationException {
        this.scenarioExecutions.stream()
            .filter(scenarioExecutions -> scenarioIdAndDatasetMatch(selectDatasetId(testCaseDataset).orElse(null), testCaseDataset.testcase().id(), scenarioExecutions))
            .findFirst()
            .ifPresent(
                scenarioExecution ->
                    this.scenarioExecutions.set(this.scenarioExecutions.indexOf(scenarioExecution),
                        new ScenarioExecutionCampaign(
                            testCaseDataset.testcase().id(),
                            testCaseDataset.testcase().metadata().title(),
                            ImmutableExecutionHistory.ExecutionSummary.builder()
                                .executionId(-1L)
                                .testCaseTitle(testCaseDataset.testcase().metadata().title())
                                .time(now())
                                .status(RUNNING)
                                .duration(0)
                                .environment(executionEnvironment)
                                .dataset(selectDatasetId(testCaseDataset))
                                .user(userId)
                                .scenarioId(testCaseDataset.testcase().id())
                                .tags(scenarioExecution.execution().tags())
                                .build()))
            );
    }

    public void updateScenarioExecutionId(ExecutionHistory.Execution storedExecution) throws UnsupportedOperationException {
        this.scenarioExecutions.stream()
            .filter(scenarioExecution -> scenarioIdAndDatasetMatch(storedExecution.dataset().orElse(null), storedExecution.scenarioId(), scenarioExecution))
            .findFirst()
            .ifPresent(scenarioExecution -> {
                var updatedExecution = ImmutableExecutionHistory.ExecutionSummary.builder()
                    .from(scenarioExecution.execution())
                    .executionId(storedExecution.executionId())
                    .build();
                this.scenarioExecutions.set(this.scenarioExecutions.indexOf(scenarioExecution),
                    new ScenarioExecutionCampaign(
                        storedExecution.scenarioId(),
                        storedExecution.testCaseTitle(),
                        updatedExecution
                    ));
            });
    }

    private boolean scenarioIdAndDatasetMatch(DataSet dataset, String scenarioId, ScenarioExecutionCampaign scenarioExecution) {
        return scenarioExecution.scenarioId().equals(scenarioId) &&
            compareDataset(scenarioExecution.execution().dataset().orElse(null), dataset);
    }

    private Optional<DataSet> selectDatasetId(TestCaseDataset testCaseDataset) {
        return ofNullable(testCaseDataset.dataset()).or(() -> ofNullable(dataset));
    }

    public void endScenarioExecution(ScenarioExecutionCampaign scenarioExecutionCampaign) throws UnsupportedOperationException {
        int index = this.scenarioExecutions.indexOf(scenarioExecutionCampaign);
        this.scenarioExecutions.set(index, scenarioExecutionCampaign);
    }

    public void endCampaignExecution() {
        if (!this.status.isFinal()) {
            this.status = findStatus(this.scenarioExecutions);
        }
    }

    public List<ScenarioExecutionCampaign> scenarioExecutionReports() {
        if (findStatus(scenarioExecutions).isFinal()) {
            scenarioExecutions.sort(ScenarioExecutionCampaign.executionIdComparator());
        }
        return unmodifiableList(scenarioExecutions);
    }

    public ServerReportStatus status() {
        return status;
    }

    public long getDuration() {
        Optional<LocalDateTime> latestExecutionEndDate = scenarioExecutions.stream()
            .map(report -> report.execution().time().plus(report.execution().duration(), ChronoUnit.MILLIS))
            .max(LocalDateTime::compareTo);

        return latestExecutionEndDate
            .map(endDate -> ChronoUnit.MILLIS.between(startDate, endDate))
            .orElse(0L);
    }

    private LocalDateTime findStartDate(List<ScenarioExecutionCampaign> scenarioExecutionReports) {
        return scenarioExecutionReports.stream()
            .filter(Objects::nonNull)
            .map(ScenarioExecutionCampaign::execution)
            .filter(Objects::nonNull)
            .map(ExecutionHistory.ExecutionProperties::time)
            .filter(Objects::nonNull)
            .reduce((time1, time2) -> {
                if (time1.isBefore(time2)) {
                    return time1;
                } else {
                    return time2;
                }
            })
            .orElse(LocalDateTime.MIN);
    }

    private ServerReportStatus findStatus(List<ScenarioExecutionCampaign> scenarioExecutionReports) {

        List<ScenarioExecutionCampaign> filteredReports = filterRetry(scenarioExecutionReports);

        ServerReportStatus foundStatus = filteredReports.stream()
            .map(ScenarioExecutionCampaign::execution)
            .filter(Objects::nonNull)
            .map(ExecutionHistory.ExecutionProperties::status)
            .collect(Collectors.collectingAndThen(Collectors.toList(), ServerReportStatus::worst));
        if (foundStatus.equals(ServerReportStatus.NOT_EXECUTED)) {
            return ServerReportStatus.STOPPED;
        }
        return foundStatus;
    }

    private List<ScenarioExecutionCampaign> filterRetry(List<ScenarioExecutionCampaign> scenarioExecutionReports) {
        return scenarioExecutionReports.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(
                Function.identity(),
                Function.identity(),
                BinaryOperator.maxBy(Comparator.comparing(s -> s.execution().time())),
                LinkedHashMap::new // Keep the insertion order
            ))
            .values()
            .stream()
            .toList();
    }

    public CampaignExecution withoutRetries() {
        return CampaignExecutionReportBuilder.builder()
            .executionId(executionId)
            .campaignId(campaignId)
            .partialExecution(partialExecution)
            .campaignName(campaignName)
            .environment(executionEnvironment)
            .dataset(dataset)
            .userId(userId)
            .startDate(startDate)
            .status(status)
            .scenarioExecutionReport(filterRetry(scenarioExecutions))
            .build();
    }

    public List<ScenarioExecutionCampaign> failedScenarioExecutions() {
        return scenarioExecutionReports().stream()
            .filter(s -> !SUCCESS.equals(s.execution().status()))
            .toList();
    }

    @Override
    public String toString() {
        return "CampaignExecution{" +
            "executionId=" + executionId +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CampaignExecution that = (CampaignExecution) o;
        return Objects.equals(executionId, that.executionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(executionId);
    }
}
