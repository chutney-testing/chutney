/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.environment.api.environment.dto;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import com.chutneytesting.environment.api.target.dto.TargetDto;
import com.chutneytesting.environment.api.variable.dto.EnvironmentVariableDto;
import com.chutneytesting.environment.api.variable.dto.EnvironmentVariableDtoMapper;
import com.chutneytesting.environment.domain.Environment;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EnvironmentDto {

    public final String name;
    public final String description;
    public final List<TargetDto> targets;
    public final List<EnvironmentVariableDto> variables;

    public EnvironmentDto(String name) {
        this.name = name;
        this.description = null;
        this.targets = emptyList();
        this.variables = emptyList();
    }

    public EnvironmentDto(String name, String description) {
        this.name = name;
        this.description = description;
        this.targets = emptyList();
        this.variables = emptyList();

    }

    public EnvironmentDto(String name, String description, List<TargetDto> targets) {
        this.name = name;
        this.description = description;
        this.targets = ofNullable(targets).map(Collections::unmodifiableList).orElse(emptyList());
        this.variables = emptyList();

    }

    @JsonCreator
    public EnvironmentDto(String name, String description, List<TargetDto> targets, List<EnvironmentVariableDto> variables) {
        this.name = name;
        this.description = description;
        this.targets = ofNullable(targets).map(Collections::unmodifiableList).orElse(emptyList());
        this.variables = ofNullable(variables).map(Collections::unmodifiableList).orElse(emptyList());

    }

    public static EnvironmentDto from(Environment environment) {
        List<TargetDto> targets = environment.targets.stream().map(TargetDto::from).collect(toList());
        List<EnvironmentVariableDto> variables = environment.variables
            .stream()
            .map(EnvironmentVariableDtoMapper.INSTANCE::fromDomain)
            .collect(toList());

        return new EnvironmentDto(environment.name, environment.description, targets, variables);
    }

    public Environment toEnvironment() {
        return Environment.builder()
            .withName(name)
            .withDescription(description)
            .withTargets(
                ofNullable(targets).orElse(emptyList()).stream().map(t -> t.toTarget(name)).collect(Collectors.toSet())
            )
            .withVariables(
                ofNullable(variables).orElse(emptyList()).stream().map(EnvironmentVariableDtoMapper.INSTANCE::toDomain).collect(Collectors.toSet())
            )
            .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnvironmentDto that = (EnvironmentDto) o;
        return Objects.equals(name, that.name) && Objects.equals(targets, that.targets) && Objects.equals(variables, that.variables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, targets, variables);
    }
}
