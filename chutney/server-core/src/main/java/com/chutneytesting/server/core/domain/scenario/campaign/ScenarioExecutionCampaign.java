/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.scenario.campaign;

import com.chutneytesting.server.core.domain.execution.history.ExecutionHistory;
import com.chutneytesting.server.core.domain.execution.report.ServerReportStatus;
import com.chutneytesting.server.core.domain.tools.DatasetUtils;
import java.util.Comparator;
import java.util.Objects;

public record ScenarioExecutionCampaign(
    String scenarioId,
    String scenarioName,
    ExecutionHistory.ExecutionSummary execution
) {

    public ServerReportStatus status() {
        return execution.status();
    }

    public static Comparator<ScenarioExecutionCampaign> executionIdComparator() {
        return Comparator.comparingLong(value -> value.execution.executionId() > 0 ? value.execution.executionId() : Long.MAX_VALUE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScenarioExecutionCampaign that = (ScenarioExecutionCampaign) o;
        return Objects.equals(scenarioId, that.scenarioId) &&
            DatasetUtils.compareDataset(execution.dataset().orElse(null), that.execution.dataset().orElse(null));
    }

    @Override
    public int hashCode() {
        return Objects.hash(scenarioId, execution.dataset());
    }
}
