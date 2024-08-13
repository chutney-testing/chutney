/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.scenario;

@SuppressWarnings("serial")
public class ScenarioNotParsableException extends RuntimeException {

    public ScenarioNotParsableException(String identifier, Exception e) {
        super("TestCase [" + identifier + "] is not valid: " + e.getMessage());
    }

}
