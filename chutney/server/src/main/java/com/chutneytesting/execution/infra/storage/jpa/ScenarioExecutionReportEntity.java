/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.execution.infra.storage.jpa;

import static java.util.Optional.ofNullable;

import com.chutneytesting.engine.domain.execution.engine.step.jackson.ReportObjectMapperConfiguration;
import com.chutneytesting.scenario.infra.raw.TagListMapper;
import com.chutneytesting.server.core.domain.dataset.DataSet;
import com.chutneytesting.server.core.domain.execution.history.ExecutionHistory;
import com.chutneytesting.server.core.domain.execution.history.ImmutableExecutionHistory;
import com.chutneytesting.server.core.domain.execution.report.ScenarioExecutionReport;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;

@Entity(name = "SCENARIO_EXECUTIONS_REPORTS")
public class ScenarioExecutionReportEntity {

    @Id
    @Column(name = "SCENARIO_EXECUTION_ID")
    private Long scenarioExecutionId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "SCENARIO_EXECUTION_ID")
    private ScenarioExecutionEntity scenarioExecution;

    @Column(name = "REPORT")
    @Basic(fetch = FetchType.LAZY)
    private String report;

    @Column(name = "VERSION")
    @Version
    private Integer version;

    public ScenarioExecutionReportEntity() {
    }

    public ScenarioExecutionReportEntity(ScenarioExecutionEntity scenarioExecution, String report) {
        this.scenarioExecutionId = scenarioExecution.id();
        this.scenarioExecution = scenarioExecution;
        this.report = report;
    }

    public void updateReport(ExecutionHistory.Execution execution) {
        report = execution.report();
    }

    public String getReport() {
        return report;
    }

    public ExecutionHistory.Execution toDomain() {
        return ImmutableExecutionHistory.Execution.builder()
            .executionId(scenarioExecutionId)
            .time(Instant.ofEpochMilli(scenarioExecution.executionTime()).atZone(ZoneId.systemDefault()).toLocalDateTime())
            .duration(scenarioExecution.duration())
            .status(scenarioExecution.status())
            .info(ofNullable(scenarioExecution.information()))
            .error(ofNullable(scenarioExecution.error()))
            .report(report)
            .testCaseTitle(scenarioExecution.scenarioTitle())
            .environment(scenarioExecution.environment())
            .user(scenarioExecution.userId())
            .dataset(ofNullable(getDatasetFromReport(report)))
            .scenarioId(scenarioExecution.scenarioId())
            .tags(TagListMapper.tagsStringToSet(scenarioExecution.tags()))
            .build();
    }

    private DataSet getDatasetFromReport(String report) {
        try { // TODO unit test \o/
            ScenarioExecutionReport scenarioExecutionReport = ReportObjectMapperConfiguration.reportObjectMapper().readValue(report, ScenarioExecutionReport.class);
            if (scenarioExecutionReport.datasetId == null &&
                (scenarioExecutionReport.constants == null || scenarioExecutionReport.constants.isEmpty()) &&
                (scenarioExecutionReport.datatable == null || scenarioExecutionReport.datatable.isEmpty())) {
                return null;
            }
            return DataSet.builder()
                .withName("")
                .withConstants(scenarioExecutionReport.constants)
                .withDatatable(scenarioExecutionReport.datatable)
                .withId(scenarioExecutionReport.datasetId)
                .build();
        } catch (IOException e) {
            return null;
        }
    }
}
