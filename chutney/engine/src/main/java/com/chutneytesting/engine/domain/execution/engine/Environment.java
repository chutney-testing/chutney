/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.engine.domain.execution.engine;

import static java.util.Collections.emptyMap;

import java.util.Map;

public record Environment(String name, Map<String, String> variables) {
    public Environment(String name) {
        this(name, emptyMap());
    }
}
