/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.junit.api;

import com.chutneytesting.environment.api.environment.dto.EnvironmentDto;
import com.chutneytesting.environment.api.target.dto.TargetDto;

public interface EnvironmentService {

    void addEnvironment(EnvironmentDto environment);

    void deleteEnvironment(String environmentName);

    void addTarget(TargetDto target);
}
