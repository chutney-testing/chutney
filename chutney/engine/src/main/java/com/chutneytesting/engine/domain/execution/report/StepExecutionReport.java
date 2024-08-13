/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.engine.domain.execution.report;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class StepExecutionReport implements Status.HavingStatus {

    public final Long executionId;
    public final String name;
    public final String environment;
    public final Long duration;
    public final Instant startDate;
    public final Status status;
    public final List<String> information;
    public final List<String> errors;
    public final List<StepExecutionReport> steps;
    public final String type;
    public final String targetName;
    public final String targetUrl;
    public final String strategy;
    public final Map<String, Object> evaluatedInputs;
    public final Map<String, Object> evaluatedInputsSnapshot;

    @JsonIgnore
    public Map<String, Object> stepResults;
    @JsonIgnore
    public Map<String, Object> stepResultsSnapshot;
    @JsonIgnore
    public Map<String, Object> scenarioContext;

    @JsonCreator
    public StepExecutionReport(Long executionId,
                               String name,
                               String environment,
                               Long duration,
                               Instant startDate,
                               Status status,
                               List<String> information,
                               List<String> errors,
                               List<StepExecutionReport> steps,
                               String type,
                               String targetName,
                               String targetUrl,
                               String strategy
    ) {
        this(executionId, name, environment, duration, startDate, status, information, errors, steps, type, targetName, targetUrl, strategy, null, null, null, null, null);
    }

    public StepExecutionReport(Long executionId,
                               String name,
                               String environment,
                               Long duration,
                               Instant startDate,
                               Status status,
                               List<String> information,
                               List<String> errors,
                               List<StepExecutionReport> steps,
                               String type,
                               String targetName,
                               String targetUrl,
                               String strategy,
                               Map<String, Object> evaluatedInputs,
                               Map<String, Object> stepResults,
                               Map<String, Object> scenarioContext,
                               Map<String, Object> evaluatedInputsSnapshot,
                               Map<String, Object> stepResultsSnapshot
    ) {
        this.executionId = executionId;
        this.name = name;
        this.environment = environment;
        this.duration = duration;
        this.startDate = startDate;
        this.status = status;
        this.information = evaluatedInputs != null ? information : emptyList();
        this.errors = evaluatedInputs != null ? errors : emptyList();
        this.steps = steps;
        this.type = type;
        this.targetName = targetName;
        this.targetUrl = targetUrl;
        this.strategy = strategy;
        this.evaluatedInputs = ofNullable(evaluatedInputs).orElse(emptyMap());
        this.evaluatedInputsSnapshot = ofNullable(evaluatedInputsSnapshot).orElse(emptyMap());
        this.stepResults = ofNullable(stepResults).orElse(emptyMap());
        this.stepResultsSnapshot = ofNullable(stepResultsSnapshot).orElse(emptyMap());
        this.scenarioContext = ofNullable(scenarioContext).orElse(emptyMap());
    }

    @Override
    public Status getStatus() {
        return status;
    }
}
