/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.api.dto;

import static java.util.Collections.emptySet;

import com.chutneytesting.server.core.domain.execution.history.ExecutionHistory;
import com.chutneytesting.server.core.domain.execution.report.ServerReportStatus;
import com.chutneytesting.server.core.domain.scenario.ExternalDataset;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScenarioExecutionReportOutlineDto {
    private String scenarioId;
    private String scenarioName;
    private ExecutionHistory.ExecutionSummary execution;

    public ScenarioExecutionReportOutlineDto(String scenarioId,
                                             String scenarioName,
                                             ExecutionHistory.ExecutionSummary execution) {
        this.scenarioId = scenarioId;
        this.scenarioName = scenarioName;
        this.execution = execution;
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public Optional<ExternalDataset> getDataset() {
        return execution.dataset();
    }

    public Long getExecutionId() {
        return execution.executionId();
    }

    public long getDuration() {
        return execution.duration();
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public LocalDateTime getStartDate() {
        return execution.time();
    }

    public ServerReportStatus getStatus() {
        return execution.status();
    }

    public String getInfo() {
        return execution.info().orElse("");
    }

    public String getError() {
        return execution.error().orElse("");
    }

    public Set<String> getTags() {
        return execution.tags().orElse(emptySet());
    }

    ExecutionHistory.ExecutionSummary getExecution() {
        return execution;
    }
}
