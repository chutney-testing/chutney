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

package com.chutneytesting.server.core.domain.scenario.campaign;

import static com.chutneytesting.server.core.domain.execution.report.ServerReportStatus.RUNNING;
import static java.time.LocalDateTime.now;
import static java.util.Collections.unmodifiableList;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

import com.chutneytesting.server.core.domain.execution.history.ExecutionHistory;
import com.chutneytesting.server.core.domain.execution.history.ImmutableExecutionHistory;
import com.chutneytesting.server.core.domain.execution.report.ServerReportStatus;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
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
    public final Optional<String> dataSetId;
    public final String userId;

    // Not mandatory
    public final LocalDateTime startDate;
    private ServerReportStatus status;
    private final List<ScenarioExecutionCampaign> scenarioExecutions;
    public final Long campaignId;

    public CampaignExecution(Long executionId,
                             String campaignName,
                             boolean partialExecution,
                             String executionEnvironment,
                             String dataSetId,
                             String userId) {
        this.executionId = executionId;
        this.campaignId = null;
        this.partialExecution = partialExecution;
        this.executionEnvironment = executionEnvironment;
        this.scenarioExecutions = new ArrayList<>();
        this.campaignName = campaignName;
        this.startDate = now();
        this.status = RUNNING;
        this.dataSetId = ofNullable(dataSetId).filter(not(String::isBlank));
        this.userId = userId;
    }

    public CampaignExecution(Long executionId,
                             Long campaignId,
                             List<ScenarioExecutionCampaign> scenarioExecutions,
                             String campaignName,
                             boolean partialExecution,
                             String executionEnvironment,
                             String dataSetId,
                             String userId) {
        this.executionId = executionId;
        this.campaignId = campaignId;
        this.campaignName = campaignName;
        this.scenarioExecutions = scenarioExecutions;
        this.startDate = findStartDate(scenarioExecutions);
        this.status = findStatus(scenarioExecutions);
        this.partialExecution = partialExecution;
        this.executionEnvironment = executionEnvironment;
        this.dataSetId = ofNullable(dataSetId).filter(not(String::isBlank));
        this.userId = userId;
    }

    CampaignExecution(
        Long executionId,
        Long campaignId,
        String campaignName,
        boolean partialExecution,
        String executionEnvironment,
        String userId,
        Optional<String> dataSetId,
        LocalDateTime startDate,
        ServerReportStatus status,
        List<ScenarioExecutionCampaign> scenarioExecutions
    ) {
        this.executionId = executionId;
        this.campaignId = campaignId;
        this.campaignName = campaignName;
        this.partialExecution = partialExecution;
        this.executionEnvironment = executionEnvironment;
        this.dataSetId = dataSetId.filter(not(String::isBlank));
        this.userId = userId;

        if (scenarioExecutions == null) {
            this.startDate = ofNullable(startDate).orElse(now());
            this.status = ofNullable(status).orElse(RUNNING);
            this.scenarioExecutions = null;
        } else {
            this.startDate = findStartDate(scenarioExecutions);
            this.status = findStatus(scenarioExecutions);
            this.scenarioExecutions = scenarioExecutions;
        }
    }

    public void initExecution(List<TestCaseDataset> testCaseDatasets, String executionEnvironment, List<String> tags) {
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
                        .datasetId(selectDatasetId(testCase))
                        .user(userId)
                        .scenarioId(testCase.testcase().id())
                        .tags(new HashSet<>(tags))
                        .build())));
    }

    public void startScenarioExecution(TestCaseDataset testCaseDataset, String executionEnvironment) throws UnsupportedOperationException {
        OptionalInt indexOpt = IntStream.range(0, this.scenarioExecutions.size())
            .filter(i -> {
                var se = this.scenarioExecutions.get(i);
                return se.scenarioId().equals(testCaseDataset.testcase().id()) &&
                    se.execution().datasetId().equals(selectDatasetId(testCaseDataset));
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
                    .datasetId(selectDatasetId(testCaseDataset))
                    .user(userId)
                    .scenarioId(testCaseDataset.testcase().id())
                    .tags(this.scenarioExecutions.get(indexOpt.getAsInt()).execution().tags())
                    .build()));
    }

    private Optional<String> selectDatasetId(TestCaseDataset testCaseDataset) {
        return ofNullable(testCaseDataset.datasetId()).or(() -> dataSetId).filter(not(String::isBlank));
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
            .setExecutionId(executionId)
            .setCampaignId(campaignId)
            .setPartialExecution(partialExecution)
            .setCampaignName(campaignName)
            .setExecutionEnvironment(executionEnvironment)
            .setDataSetId(dataSetId.orElse(null))
            .setUserId(userId)
            .setStartDate(startDate)
            .setStatus(status)
            .setScenarioExecutionReport(filterRetry(scenarioExecutions))
            .build();
    }

    public List<ScenarioExecutionCampaign> failedScenarioExecutions() {
        return scenarioExecutionReports().stream()
            .filter(s -> !ServerReportStatus.SUCCESS.equals(s.execution().status()))
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
