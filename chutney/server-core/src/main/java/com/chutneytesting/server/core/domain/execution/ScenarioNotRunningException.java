/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.execution;

public class ScenarioNotRunningException extends RuntimeException {

    public ScenarioNotRunningException(String scenarioId) {
        super("Scenario [" + scenarioId + "] is not running");
    }
}
