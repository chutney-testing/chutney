/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.scenario.campaign;

import static com.chutneytesting.server.core.domain.dataset.ExternalDatasetEntityMapper.compareExternalDataset;
import static com.chutneytesting.server.core.domain.execution.report.ServerReportStatus.RUNNING;
import static com.chutneytesting.server.core.domain.execution.report.ServerReportStatus.SUCCESS;
import static java.time.LocalDateTime.now;
import static java.util.Collections.unmodifiableList;
import static java.util.Optional.ofNullable;

import com.chutneytesting.server.core.domain.execution.history.ExecutionHistory;
import com.chutneytesting.server.core.domain.execution.history.ImmutableExecutionHistory;
import com.chutneytesting.server.core.domain.execution.report.ServerReportStatus;
import com.chutneytesting.server.core.domain.scenario.ExternalDataset;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CampaignExecution {

    // Mandatory
    public final Long executionId;
    public final String campaignName;
    public final boolean partialExecution;
    public final String executionEnvironment;
    public final ExternalDataset externalDataset;
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
        ExternalDataset externalDataset,
        LocalDateTime startDate,
        ServerReportStatus status,
        List<ScenarioExecutionCampaign> scenarioExecutions
    ) {
        this.executionId = executionId;
        this.campaignId = campaignId;
        this.campaignName = campaignName;
        this.partialExecution = partialExecution;
        this.executionEnvironment = executionEnvironment;
        this.externalDataset = externalDataset;
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
                        .externalDataset(selectDatasetId(testCase))
                        .user(userId)
                        .scenarioId(testCase.testcase().id())
                        .build())));
    }

    public void startScenarioExecution(TestCaseDataset testCaseDataset, String executionEnvironment) throws UnsupportedOperationException {
        OptionalInt indexOpt = IntStream.range(0, this.scenarioExecutions.size())
            .filter(i -> {
                var se = this.scenarioExecutions.get(i);
                return se.scenarioId().equals(testCaseDataset.testcase().id()) &&
                    se.execution().externalDataset().equals(selectDatasetId(testCaseDataset));
            })
            .findFirst();
        this.scenarioExecutions.set(indexOpt.getAsInt(),
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
                    .externalDataset(selectDatasetId(testCaseDataset))
                    .user(userId)
                    .scenarioId(testCaseDataset.testcase().id())
                    .tags(this.scenarioExecutions.get(indexOpt.getAsInt()).execution().tags())
                    .build()));
    }

    public void updateScenarioExecutionId(ExecutionHistory.Execution storedExecution) throws UnsupportedOperationException {
        OptionalInt indexOpt = IntStream.range(0, this.scenarioExecutions.size())
            .filter(i -> {
                var se = this.scenarioExecutions.get(i);
                return se.scenarioId().equals(storedExecution.scenarioId()) &&
                    compareExternalDataset(se.execution().externalDataset().orElse(null), storedExecution.externalDataset().orElse(null));
            })
            .findFirst();
        var scenarioExecution = this.scenarioExecutions.get(indexOpt.getAsInt()).execution();
        this.scenarioExecutions.set(indexOpt.getAsInt(),
            new ScenarioExecutionCampaign(
                storedExecution.scenarioId(),
                storedExecution.testCaseTitle(),
                ImmutableExecutionHistory.ExecutionSummary.builder()
                    .from(scenarioExecution)
                    .executionId(storedExecution.executionId())
                    .build()));
    }

    private Optional<ExternalDataset> selectDatasetId(TestCaseDataset testCaseDataset) {
        return ofNullable(testCaseDataset.dataset()).or(() -> ofNullable(externalDataset));
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
            .externalDataset(externalDataset)
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
