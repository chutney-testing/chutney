/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.environment.api.variable;


import com.chutneytesting.environment.api.variable.dto.EnvironmentVariableDto;
import com.chutneytesting.environment.domain.exception.EnvVariableNotFoundException;
import com.chutneytesting.environment.domain.exception.EnvironmentNotFoundException;
import com.chutneytesting.environment.domain.exception.VariableAlreadyExistingException;
import java.util.List;

public interface EnvironmentVariableApi {

    void addVariable(List<EnvironmentVariableDto> values) throws EnvironmentNotFoundException, VariableAlreadyExistingException;

    void updateVariable(String key, List<EnvironmentVariableDto> values) throws EnvironmentNotFoundException, EnvVariableNotFoundException;

    void deleteVariable(String key) throws  EnvVariableNotFoundException;

}
