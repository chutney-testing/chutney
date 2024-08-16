/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.execution.infra.execution;

class ScenarioContent {

    UnmarshalledStepDefinition scenario;

    public UnmarshalledStepDefinition getScenario() {
        return scenario;
    }

    public void setScenario(UnmarshalledStepDefinition scenario) {
        this.scenario = scenario;
    }

}
