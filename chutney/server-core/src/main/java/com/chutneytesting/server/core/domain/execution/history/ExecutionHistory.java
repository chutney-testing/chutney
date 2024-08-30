/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.execution.history;

import com.chutneytesting.server.core.domain.dataset.DataSet;
import com.chutneytesting.server.core.domain.execution.report.ServerReportStatus;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecution;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@Value.Enclosing
@JsonSerialize(as = ImmutableExecutionHistory.class)
public interface ExecutionHistory {

    @Value.Parameter
    String scenarioId();

    @Value.Parameter
    List<Execution> history();

    interface ExecutionProperties {
        LocalDateTime time();

        long duration();

        ServerReportStatus status();

        Optional<String> info();

        Optional<String> error();

        String testCaseTitle();

        String environment();

        Optional<DataSet> dataset();

        String user();

        Optional<CampaignExecution> campaignReport();

        Optional<Set<String>> tags();
    }

    interface Attached {
        Long executionId();
    }

    interface WithScenario {
        String scenarioId();
    }

    @Value.Immutable
    interface DetachedExecution extends ExecutionProperties, HavingReport {

        default Execution attach(long executionId, String scenarioId) {
            return ImmutableExecutionHistory.Execution.builder()
                .from((ExecutionProperties) this)
                .from((HavingReport) this)
                .executionId(executionId)
                .scenarioId(scenarioId)
                .build();
        }
    }

    @Value.Immutable
    interface ExecutionSummary extends ExecutionProperties, Attached, WithScenario {
    }

    @Value.Immutable
    interface Execution extends ExecutionProperties, HavingReport, Attached, WithScenario {

        default ExecutionSummary summary() {
            return ImmutableExecutionHistory.ExecutionSummary.builder()
                .from((ExecutionProperties) this)
                .from((Attached) this)
                .from((WithScenario) this)
                .build();
        }
    }
}
