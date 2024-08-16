/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.execution.state;

import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
public interface RunningScenarioState {
    @Value.Parameter
    String scenarioId();

    @Value.Derived
    default Instant startTime() {
        return Instant.now();
    }

    static RunningScenarioState of(String scenarioId) {
        return ImmutableRunningScenarioState.of(scenarioId);
    }
}
