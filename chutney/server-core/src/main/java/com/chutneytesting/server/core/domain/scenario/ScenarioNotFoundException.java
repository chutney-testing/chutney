/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.scenario;

@SuppressWarnings("serial")
public class ScenarioNotFoundException  extends RuntimeException {

    public ScenarioNotFoundException(String scenarioId) {
        super("Scenario [" + scenarioId + "] not found !");
    }

    public ScenarioNotFoundException(String scenarioId, Integer version) {
        super("Scenario [" + scenarioId + "] with version [" + version + "] not found !");
    }
}
