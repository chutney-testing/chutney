/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.junit.engine;

import com.chutneytesting.environment.api.environment.EnvironmentApi;
import com.chutneytesting.environment.api.environment.dto.EnvironmentDto;
import com.chutneytesting.environment.api.target.TargetApi;
import com.chutneytesting.environment.api.target.dto.TargetDto;
import com.chutneytesting.junit.api.EnvironmentService;

public class EnvironmentServiceImpl implements EnvironmentService {

    private final EnvironmentApi environmentApi;
    private final TargetApi targetApi;

    public EnvironmentServiceImpl(EnvironmentApi delegate, TargetApi targetApi) {
        this.environmentApi = delegate;
        this.targetApi = targetApi;
    }

    @Override
    public void addEnvironment(EnvironmentDto environment) {
        environmentApi.createEnvironment(environment);
    }

    @Override
    public void deleteEnvironment(String environmentName) {
        environmentApi.deleteEnvironment(environmentName);
    }

    @Override
    public void addTarget(TargetDto target) {
        targetApi.addTarget(target);
    }
}
