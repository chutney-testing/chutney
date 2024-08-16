/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.environment.api.target;

import static java.util.stream.Collectors.toList;

import com.chutneytesting.environment.api.target.dto.TargetDto;
import com.chutneytesting.environment.domain.EnvironmentService;
import com.chutneytesting.environment.domain.TargetFilter;
import com.chutneytesting.environment.domain.exception.AlreadyExistingTargetException;
import com.chutneytesting.environment.domain.exception.EnvironmentNotFoundException;
import com.chutneytesting.environment.domain.exception.TargetNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class EmbeddedTargetApi implements  TargetApi {

    private final EnvironmentService environmentService;

    public EmbeddedTargetApi(EnvironmentService environmentService) {
        this.environmentService = environmentService;
    }

    @Override
    public List<TargetDto> listTargets(TargetFilter filters) throws EnvironmentNotFoundException {
        return environmentService.listTargets(filters).stream()
            .map(TargetDto::from)
            .sorted(Comparator.comparing(t -> t.name))
            .collect(toList());
    }

    @Override
    public Set<String> listTargetsNames() throws EnvironmentNotFoundException {
        return environmentService.listTargetsNames();
    }

    @Override
    public TargetDto getTarget(String environmentName, String targetName) throws EnvironmentNotFoundException, TargetNotFoundException {
        return TargetDto.from(environmentService.getTarget(environmentName, targetName));
    }

    @Override
    public void addTarget(TargetDto targetMetadataDto) throws EnvironmentNotFoundException, AlreadyExistingTargetException {
        environmentService.addTarget(targetMetadataDto.toTarget());
    }

    @Override
    public TargetDto importTarget(String environmentName, TargetDto targetDto) {
        environmentService.addTarget(targetDto.toTarget(environmentName));
        return targetDto;
    }

    @Override
    public void updateTarget(String targetName, TargetDto targetMetadataDto) throws EnvironmentNotFoundException, TargetNotFoundException {
        environmentService.updateTarget(targetName, targetMetadataDto.toTarget());
    }

    @Override
    public void deleteTarget(String environmentName, String targetName) throws EnvironmentNotFoundException, TargetNotFoundException {
        environmentService.deleteTarget(environmentName, targetName);
    }

    @Override
    public void deleteTarget(String targetName) throws EnvironmentNotFoundException, TargetNotFoundException {
        environmentService.deleteTarget(targetName);
    }


}
