/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.environment.api.variable;

import com.chutneytesting.environment.api.variable.dto.EnvironmentVariableDto;
import com.chutneytesting.environment.api.variable.dto.EnvironmentVariableDtoMapper;
import com.chutneytesting.environment.domain.EnvironmentService;
import com.chutneytesting.environment.domain.EnvironmentVariable;
import com.chutneytesting.environment.domain.exception.EnvVariableNotFoundException;
import com.chutneytesting.environment.domain.exception.EnvironmentNotFoundException;
import com.chutneytesting.environment.domain.exception.VariableAlreadyExistingException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class EmbeddedVariableApi implements EnvironmentVariableApi {

    private final EnvironmentService environmentService;

    private final EnvironmentVariableDtoMapper variableDtoMapper;

    public EmbeddedVariableApi(EnvironmentService environmentService) {
        this.environmentService = environmentService;
        this.variableDtoMapper = EnvironmentVariableDtoMapper.INSTANCE;
    }


    @Override
    public void addVariable(List<EnvironmentVariableDto> values) throws EnvironmentNotFoundException, VariableAlreadyExistingException {
        environmentService.addVariable(variableDtoMapper.toDomains(values));
    }

    @Override
    public void updateVariable(String key, List<EnvironmentVariableDto> values) throws EnvironmentNotFoundException, EnvVariableNotFoundException {
        List<EnvironmentVariable> variables = variableDtoMapper.toDomains(values);
        Map<Boolean, List<EnvironmentVariable>> partitionedVariables = variables.stream().collect(Collectors.partitioningBy(item -> StringUtils.isNotBlank(item.value())));
        List<EnvironmentVariable> toBeCreatedOrUpdated = partitionedVariables.get(true);
        List<EnvironmentVariable> toBeDeleted = partitionedVariables.get(false);
        environmentService.createOrUpdateVariable(key, toBeCreatedOrUpdated);
        environmentService.deleteVariable(key, toBeDeleted.stream().map(EnvironmentVariable::env).toList());
    }

    @Override
    public void deleteVariable(String key) throws EnvVariableNotFoundException {
        environmentService.deleteVariable(key);
    }
}
