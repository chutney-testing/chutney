/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.environment.infra;

import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

import com.chutneytesting.environment.domain.Environment;
import com.chutneytesting.environment.domain.EnvironmentVariable;
import com.chutneytesting.environment.domain.Target;
import java.util.Set;

public class JsonEnvironment {

    public String name;
    public String description;
    public Set<JsonTarget> targets;
    public Set<JsonEnvVariable> variables;

    public JsonEnvironment() {
    }

    private JsonEnvironment(String name, String description, Set<JsonTarget> targets, Set<JsonEnvVariable> variables) {
        this.name = name;
        this.description = description;
        this.targets = targets;
        this.variables = variables;
    }

    public static JsonEnvironment from(Environment environment) {
        Set<JsonTarget> targets = environment.targets.stream().map(JsonTarget::from).collect(toSet());
        Set<JsonEnvVariable> variables = JsonEnvVariableMapper.INSTANCE.fromDomains(environment.variables);
        return new JsonEnvironment(environment.name, environment.description, targets, variables);
    }

    public Environment toEnvironment() {
        Set<EnvironmentVariable> variables = ofNullable(this.variables).orElse(emptySet()).stream().map(item -> JsonEnvVariableMapper.INSTANCE.toDomain(item, name)).collect(toSet());
        Set<Target> targets = ofNullable(this.targets).orElse(emptySet()).stream().map(t -> t.toTarget(name)).collect(toSet());
        return Environment.builder()
            .withName(name)
            .withDescription(description)
            .withTargets(targets)
            .withVariables(variables)
            .build();
    }
}
