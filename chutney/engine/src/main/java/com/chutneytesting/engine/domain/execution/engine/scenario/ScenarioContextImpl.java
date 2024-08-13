/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.engine.domain.execution.engine.scenario;

import java.util.LinkedHashMap;

@SuppressWarnings("serial")
public class ScenarioContextImpl extends LinkedHashMap<String, Object> implements ScenarioContext {

    public ScenarioContextImpl() {
        super();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(String key, T defaultValue) {
        return (T) super.getOrDefault(key, defaultValue);
    }

}
